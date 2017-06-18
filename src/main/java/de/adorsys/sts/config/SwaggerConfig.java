package de.adorsys.sts.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * fpo
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket tokenApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.withClassAnnotation(TokenResource.class)).paths(PathSelectors.any()).build()
				.apiInfo(new ApiInfo("Security Token Exchange Endpoint",
						"This is the Token Exchange Endpoint API as defined in https://tools.ietf.org/html/draft-ietf-oauth-token-exchange-08.",
						"1.0", "urn:tos", new Contact("adorsys GmbH & Co. KG", null, "fpo@adorsys.de"),
						"Apache License, Version 2.0", "https://www.apache.org/licenses/LICENSE-2.0.html"))
				.securitySchemes(securitySchemes(new ApiKey("Authorization", "BearerToken", "header")));
	}
	
	@Bean
	public Docket adminApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("admin")
				.select()
				.apis(RequestHandlerSelectors.withClassAnnotation(AdminResource.class)).paths(PathSelectors.any()).build()
				.apiInfo(new ApiInfo("Simple Multibanking Admin Backend",
						"This is the Token Exchange Admin API. Use this to create bank login for end users. In order to authorize this API, use a baic auth token like: Basic YWRtaW46cGFzc3dvcmQ=",
						"1.0", "urn:tos", new Contact("Adorsys GmbH & Co. KG", null, "fpo@adorsys.de"),
						"Apache License, Version 2.0", "https://www.apache.org/licenses/LICENSE-2.0.html"))
				.securitySchemes(securitySchemes(new ApiKey("Authorization", "Basic", "header")));
	}
	
	private List<? extends SecurityScheme> securitySchemes(ApiKey apiKey) {
		ArrayList<ApiKey> arrayList = new ArrayList<>();
		arrayList.add(apiKey);
		return arrayList;
	}
}