package de.adorsys.sts.worksheetloader;

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
		String login;
		String serverAndEncKeyList;
		String password;
		
		Cell cell = row.getCell(0);
		if(cell != null && StringUtils.isNotBlank(cell.getStringCellValue())){	
			login = cell.getStringCellValue().trim();
		}else{
			return;
		}
		
		cell = row .getCell(1);
		if(cell != null && StringUtils.isNotBlank(cell.getStringCellValue())){	
			password = cell.getStringCellValue().trim();
		}else{
			return;
		}
		
		cell = row .getCell(2);
		if(cell != null && StringUtils.isNotBlank(cell.getStringCellValue())){
			serverAndEncKeyList = cell.getStringCellValue().trim();
		}else{
			return;
		}
		
		KeyCredentials keyCredentials = namingPolicy.newKeyCredntials(login, password);
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
		// Retrieve the comma separated list of resource server and corresponding user encryption key.
		String[] serverAndEncKeyArray = StringUtils.split(serverAndEncKeyList, ",");
		for (String serverAndEncKey : serverAndEncKeyArray) {
			String[] perUser = StringUtils.split(serverAndEncKey, "=");
			if(perUser.length<2) continue;
			String serverAudienceName=perUser[0];
			String userEncKey=perUser[1];
			resourceServerProcessor.storeUserCredentials(userDataService, userEncKey, serverAudienceName);
		}
	}
}
