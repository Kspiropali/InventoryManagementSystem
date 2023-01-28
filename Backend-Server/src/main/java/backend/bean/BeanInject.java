/*
package backend.bean;


import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import javax.sql.DataSource;
import java.util.Arrays;

public class BeanInject implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry (BeanDefinitionRegistry registry) throws BeansException {

        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(DataSource.class);
        genericBeanDefinition.getPropertyValues().getPropertyValues();
        System.out.println(Arrays.toString(genericBeanDefinition.getPropertyValues().getPropertyValues()));
        registry.registerBeanDefinition("jdbcCustom", genericBeanDefinition);
    }

    @Override
    public void postProcessBeanFactory (@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //no op
    }
}*/
