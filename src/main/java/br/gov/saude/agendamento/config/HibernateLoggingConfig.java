package br.gov.saude.agendamento.config;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateLoggingConfig {

    @Bean
    public StatementInspector statementInspector() {
        return new JpaSqlCaptureInspector();
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(StatementInspector statementInspector) {
        return properties -> properties.put(AvailableSettings.STATEMENT_INSPECTOR, statementInspector);
    }
}

