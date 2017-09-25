package de.adorsys.sts.starter.config;


import de.adorsys.sts.common.ServerKeyManagerFactory;
import org.adorsys.encobject.filesystem.FsPersistenceFactory;
import org.adorsys.jjwk.serverkey.ServerKeyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class ServerKeyManagerConfig {

    @Autowired
    private FsPersistenceFactory persFactory;

    private ServerKeyManager serverKeyManager;

    @PostConstruct
    public void initServerKeyManager() {
        serverKeyManager = new ServerKeyManagerFactory(persFactory).build();
    }

    @Bean
    public ServerKeyManager getServerKeyManager() {
        return serverKeyManager;
    }
}
