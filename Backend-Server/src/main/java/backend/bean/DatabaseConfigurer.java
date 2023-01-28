/*
package backend.bean;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@NoArgsConstructor
public class DatabaseConfigurer {



    @Bean(name = "jdbcCustom")
    @Autowired
    public JdbcTemplate jdbcTemplate(@Qualifier("databaseConfig") DataSource dynamicDatabase) {
        return new JdbcTemplate(dynamicDatabase);
    }
}*/
