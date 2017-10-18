package de.adorsys.sts.worksheetloader;

import com.google.common.collect.Lists;
import de.adorsys.sts.resourceserver.service.ResourceServerProcessorService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoginLoader {
	Logger LOG = LoggerFactory.getLogger(LoginLoader.class);

    @Autowired
    private ResourceServerProcessorService resourceServerProcessorService;

	public void update(Row row) {
		Optional<ReadUserCredentials> userCredentialsFromRow = parseFromRow(row);

		if(userCredentialsFromRow.isPresent()) {
			ReadUserCredentials readUserCredentials = userCredentialsFromRow.get();

			List<ReadUserCredentials.ServerAndUserEncKey> serverAndUserEncKeyList = readUserCredentials.getServerAndUserEncKeyList();

			for(ReadUserCredentials.ServerAndUserEncKey serverAndUserEncKey : serverAndUserEncKeyList) {
				resourceServerProcessorService.storeCredentials(readUserCredentials.getLogin(), readUserCredentials.getPassword(), serverAndUserEncKey.getServerAudienceName(), serverAndUserEncKey.getUserEncKey());
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
