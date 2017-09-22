package de.adorsys.sts.starter.config;

import de.adorsys.sts.admin.EnableAdmin;
import de.adorsys.sts.common.rserver.ResourceServerManager;
import de.adorsys.sts.common.token.ResourceServerProcessor;
import de.adorsys.sts.pop.EnablePOP;
import de.adorsys.sts.serverinfo.EnableServerInfo;
import de.adorsys.sts.token.passwordgrant.EnablePasswordGrant;
import de.adorsys.sts.token.tokenexchange.EnableTokenExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnablePOP
@EnableTokenExchange
@EnablePasswordGrant
@EnableAdmin
@EnableServerInfo
public class SecureTokenServiceConfiguration {

    @Bean
    public ResourceServerProcessor resourceServerProcessor() {
        return new ResourceServerProcessor();
    }

    @Bean
    public ResourceServerManager resourceServerManager() {
        return new ResourceServerManager();
    }
}
