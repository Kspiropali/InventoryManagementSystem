/*
package backend.bean;


import com.zaxxer.hikari.util.DriverDataSource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Properties;


@ComponentScan("backend.bean")
@Configuration
public class BeanConfigurer implements ApplicationContextAware {

    private final ApplicationContext applicationContext;

    public BeanConfigurer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    public void removeExistingAndAddNewBean(String beanId) {

        AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory;

        registry.removeBeanDefinition("databaseConfig");
        //registry.removeBeanDefinition("jdbcCustom");

        DefaultListableBeanFactory context = new DefaultListableBeanFactory();

        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(DataSource.class);

        MutablePropertyValues pv = new MutablePropertyValues();
        pv.add("username", "kspir");
        pv.add("password", "");
        pv.add("url", "jdbc:postgresql://localhost:5432/springboot");
        pv.add("dialect", "org.hibernate.dialect.PostgreSQLDialect");
        genericBeanDefinition.setPropertyValues(pv);
        context.registerBeanDefinition("databaseConfig", genericBeanDefinition);

        //GenericBeanDefinition genericBeanDefinition1 = new GenericBeanDefinition();
        //genericBeanDefinition1.setBeanClass(JdbcTemplate.class);
        //context.registerBeanDefinition("jdbcCustom", genericBeanDefinition1);
        DriverDataSource driverDataSource = new DriverDataSource("jdbc:postgresql://localhost:5432/springboot","", new Properties(),"kspir","");
        jdbcTemplate(driverDataSource);
    }

    @Bean(name = "jdbcCustom")
    @Autowired
    public JdbcTemplate jdbcTemplate(@Qualifier("databaseConfig") DataSource dynamicDatabase) {
        return new JdbcTemplate(dynamicDatabase);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {

    }
}*/
