package de.adorsys.sts.starter;

import de.adorsys.sts.common.EnableSTS;
import de.adorsys.sts.pop.EnablePOP;
import de.adorsys.sts.token.tokenexchange.EnableTokenExchange;
import de.adorsys.sts.token.passwordgrant.EnablePasswordGrant;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSTS
@EnablePOP
@EnableTokenExchange
@EnablePasswordGrant
public class SecureTokenServiceConfiguration {
}
