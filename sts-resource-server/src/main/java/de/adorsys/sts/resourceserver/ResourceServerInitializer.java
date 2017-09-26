package de.adorsys.sts.resourceserver;

import de.adorsys.sts.resourceserver.provider.EnvironmentVariableResourceServersProvider;
import de.adorsys.sts.resourceserver.provider.ResourceServersProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value=ResourceServerInitializer.ORDER)
public class ResourceServerInitializer implements ApplicationRunner {
	public static final int ORDER =  1;
	@Autowired
	private ResourceServerManager resourceServerManager;

	private ResourceServersProvider resourceServersProvider = new EnvironmentVariableResourceServersProvider();

	@Override
	public void run(ApplicationArguments args) throws Exception {
		resourceServerManager.addResourceServers(resourceServersProvider.get());
	}
}
