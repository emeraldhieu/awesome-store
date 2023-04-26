package com.emeraldhieu.awesomestore.product.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories({"com.emeraldhieu.awesomestore.product.logic"})
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfiguration {
}
