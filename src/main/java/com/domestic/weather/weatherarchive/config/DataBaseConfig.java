package com.domestic.weather.weatherarchive.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = "com.domestic.weather.weatherarchive")
@EnableTransactionManagement
public class DataBaseConfig {

    @Bean("mySqlProperties")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("dataSource")
    public DataSource dataSource(@Qualifier("mySqlProperties") DataSourceProperties properties) {
        return properties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("dataSource") DataSource db) {
        return builder.dataSource(db)
                .packages("com.domestic.weather.weatherarchive")
                .persistenceUnit("mySql")
                .build();
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory db1EntityManagerFactory) {
        return new JpaTransactionManager(db1EntityManagerFactory);
    }
}
