package de.adorsys.sts.starter;

import de.adorsys.sts.common.EnableSTS;
import de.adorsys.sts.pop.EnablePOP;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSTS
@EnablePOP
public class SecureTokenServiceConfiguration {
}
