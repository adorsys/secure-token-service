package de.adorsys.sts.tests.config;

import de.adorsys.sts.serverinfo.EnableServerInfo;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableAutoConfiguration(exclude = MongoAutoConfiguration.class)
@EnableServerInfo
@EnableSwagger2
public class WithServerInfo {
}
