package de.adorsys.sts.starter.config;

import de.adorsys.sts.common.config.AdminResource;
import de.adorsys.sts.common.config.TokenResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * fpo
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/api-docs/v2/api-docs", "/v2/api-docs");
		registry.addRedirectViewController("/api-docs/swagger-resources/configuration/ui","/swagger-resources/configuration/ui");
		registry.addRedirectViewController("/api-docs/swagger-resources/configuration/security","/swagger-resources/configuration/security");
		registry.addRedirectViewController("/api-docs/swagger-resources", "/swagger-resources");
		registry.addRedirectViewController("/api-docs/", "/api-docs/swagger-ui.html");
		registry.addRedirectViewController("/api-docs", "/api-docs/swagger-ui.html");
		registry.addRedirectViewController("/api-docs/index.html", "/api-docs/swagger-ui.html");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.
				addResourceHandler("/api-docs/swagger-ui.html**").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
		registry.
				addResourceHandler("/api-docs/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

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