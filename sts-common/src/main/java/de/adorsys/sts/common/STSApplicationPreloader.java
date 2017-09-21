package de.adorsys.sts.common;

import java.io.InputStream;

import de.adorsys.sts.common.loader.DataSheetLoader;
import de.adorsys.sts.common.rserver.ResourceServerInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
