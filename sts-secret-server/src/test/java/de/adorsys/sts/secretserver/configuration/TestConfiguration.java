package de.adorsys.sts.secretserver.configuration;

import de.adorsys.sts.decryption.EnableDecryption;
import de.adorsys.sts.secretserver.EnableSecretServer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties
@EnableAutoConfiguration
@ComponentScan(basePackages = {"de.adorsys.sts.secretserver.helper"})
@EnableSecretServer
@EnableDecryption
public class TestConfiguration {

    @Bean
    public TestRestTemplate testRestTemplate() {
        return new TestRestTemplate();
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUsername("db_user");
        dataSource.setPassword("db_user@123");
        dataSource.setUrl(
                "jdbc:h2:mem:AZ;INIT=CREATE SCHEMA IF NOT EXISTS sts;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        return dataSource;
    }}
