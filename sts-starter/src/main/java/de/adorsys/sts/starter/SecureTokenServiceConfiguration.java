package de.adorsys.sts.starter;

import de.adorsys.sts.common.EnableSTS;
import de.adorsys.sts.pop.EnablePOP;
import de.adorsys.sts.tokenexchange.EnableTokenExchange;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSTS
@EnablePOP
@EnableTokenExchange
public class SecureTokenServiceConfiguration {
}
