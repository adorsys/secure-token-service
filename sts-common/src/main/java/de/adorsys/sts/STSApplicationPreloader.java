package de.adorsys.sts;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.adorsys.sts.loader.DataSheetLoader;
import de.adorsys.sts.rserver.ResourceServerInitializer;

/**
 * @author fpo
 *
 */
@Component
@Order(value=ResourceServerInitializer.ORDER + 1) 
public class STSApplicationPreloader implements ApplicationRunner {
	
	@Autowired
	private DataSheetLoader dataSheetLoader ;

	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		InputStream loadFile = dataSheetLoader.loadFile();
		dataSheetLoader.loadDataSheet(loadFile);
	}

}
