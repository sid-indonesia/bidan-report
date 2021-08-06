package org.sidindonesia.bidanreport.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories("org.sidindonesia.bidanreport.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfiguration {

}
