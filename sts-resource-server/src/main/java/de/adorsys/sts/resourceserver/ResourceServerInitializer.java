package de.adorsys.sts.resourceserver;

import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
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

	private final ResourceServerRepository repository;
	private final ResourceServersProvider provider;

	@Autowired
	public ResourceServerInitializer(ResourceServerRepository repository, ResourceServersProvider provider) {
		this.repository = repository;
		this.provider = provider;
	}


	@Override
	public void run(ApplicationArguments args) throws Exception {
		repository.addAll(provider.get());
	}
}
