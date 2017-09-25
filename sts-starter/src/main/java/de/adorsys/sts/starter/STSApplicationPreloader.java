package de.adorsys.sts.starter;

import de.adorsys.sts.resourceserver.ResourceServerInitializer;
import de.adorsys.sts.worksheetloader.DataSheetLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @author fpo
 *
 */
@Component
@Order(value= ResourceServerInitializer.ORDER + 1)
public class STSApplicationPreloader implements ApplicationRunner {
	
	@Autowired
	private DataSheetLoader dataSheetLoader ;

	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		InputStream loadFile = dataSheetLoader.loadFile();
		dataSheetLoader.loadDataSheet(loadFile);
	}

}
