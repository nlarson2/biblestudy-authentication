package com.larson.authentication.Security.Config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {


    @Value("${DB_URL}")
    private String url;

    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    @Value("${DB_USERNAME}")
    private String username;

    @Value("${DB_PASSWORD}")
    private String dbPassword;
     
    @Bean
    public DataSource getDataSource() {
        return DataSourceBuilder.create()
        .driverClassName(driver)
        .url(url)
        .username(username)
        .password(dbPassword)
        .build();
    }

}
