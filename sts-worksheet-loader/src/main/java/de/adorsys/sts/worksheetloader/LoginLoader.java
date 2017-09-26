package de.adorsys.sts.worksheetloader;

import com.google.common.collect.Lists;
import de.adorsys.sts.common.user.DefaultObjectMapper;
import de.adorsys.sts.resourceserver.ResourceServerProcessor;
import org.adorsys.encobject.domain.KeyCredentials;
import org.adorsys.encobject.filesystem.FsPersistenceFactory;
import org.adorsys.encobject.service.KeystoreNotFoundException;
import org.adorsys.encobject.userdata.ObjectPersistenceAdapter;
import org.adorsys.encobject.userdata.UserDataNamingPolicy;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.adorsys.sts.common.user.UserDataService;

import java.util.List;
import java.util.Optional;

@Service
public class LoginLoader {
	Logger LOG = LoggerFactory.getLogger(LoginLoader.class);

	@Autowired
	private UserDataNamingPolicy namingPolicy;
    @Autowired
    private FsPersistenceFactory persFactory;
    @Autowired
    private ResourceServerProcessor resourceServerProcessor;

    private static DefaultObjectMapper objectMapper = new DefaultObjectMapper();

	public void update(Row row) {
		Optional<ReadUserCredentials> userCredentialsFromRow = parseFromRow(row);

		if(userCredentialsFromRow.isPresent()) {
			ReadUserCredentials readUserCredentials = userCredentialsFromRow.get();
			KeyCredentials keyCredentials = namingPolicy.newKeyCredntials(readUserCredentials.getLogin(), readUserCredentials.getPassword());

			ObjectPersistenceAdapter objectPersistenceAdapter = new ObjectPersistenceAdapter(persFactory.getEncObjectService(), keyCredentials, objectMapper);
			// Check if we have this user in the storage. If so user the record, if not create one.
			UserDataService userDataService = new UserDataService(namingPolicy, objectPersistenceAdapter);
			if(!userDataService.hasAccount()){
				try {
					userDataService.addAccount();
				} catch (KeystoreNotFoundException e) {
					throw new IllegalStateException();
				}
			}

			List<ReadUserCredentials.ServerAndUserEncKey> serverAndUserEncKeyList = readUserCredentials.getServerAndUserEncKeyList();

			for(ReadUserCredentials.ServerAndUserEncKey serverAndUserEncKey : serverAndUserEncKeyList) {
				resourceServerProcessor.storeUserCredentials(userDataService, serverAndUserEncKey.getUserEncKey(), serverAndUserEncKey.getServerAudienceName());
			}
		}
	}

	private Optional<ReadUserCredentials> parseFromRow(Row row) {
		Optional<ReadUserCredentials> userCredentials = Optional.empty();

		Optional<String> login = readText(row, 0);
		if(!login.isPresent()) {
			return userCredentials;
		}

		Optional<String> password = readText(row, 1);
		if(!password.isPresent()) {
			return userCredentials;
		}

		List<ReadUserCredentials.ServerAndUserEncKey> serverAndEncKeyList = readFrom(row);
		if(serverAndEncKeyList.isEmpty()) {
			return userCredentials;
		}

		return Optional.of(
				ReadUserCredentials.builder()
				.login(login.get())
				.password(password.get())
				.serverAndUserEncKeyList(serverAndEncKeyList)
				.build()
		);
	}

	private List<ReadUserCredentials.ServerAndUserEncKey> readFrom(Row row) {
		List<ReadUserCredentials.ServerAndUserEncKey> serverAndUserEncKeyList = Lists.newArrayList();

		Optional<String> serverAndEncKeyList = readText(row, 2);
		if(!serverAndEncKeyList.isPresent()) {
			return serverAndUserEncKeyList;
		}

		String[] serverAndEncKeyArray = StringUtils.split(serverAndEncKeyList.get(), ",");
		for (String serverAndEncKey : serverAndEncKeyArray) {
			String[] perUser = StringUtils.split(serverAndEncKey, "=");

			if(perUser.length >= 2) {
				ReadUserCredentials.ServerAndUserEncKey serverAndUserEncKey = ReadUserCredentials.ServerAndUserEncKey.builder()
						.serverAudienceName(perUser[0])
						.userEncKey(perUser[1])
						.build();

				serverAndUserEncKeyList.add(serverAndUserEncKey);
			}
		}

		return serverAndUserEncKeyList;
	}

	private Optional<String> readText(Row row, int cellIndex) {
		Optional<String> text = Optional.empty();

		Cell cell = row.getCell(cellIndex);
		if(cell != null && StringUtils.isNotBlank(cell.getStringCellValue())){
			String value = cell.getStringCellValue().trim();
			text = Optional.of(value);
		}

		return text;
	}
}
