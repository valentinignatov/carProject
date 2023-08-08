package com.cars.configuration.quartz;

import com.sun.source.util.TaskListener;
import lombok.var;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class QuartzConfiguration implements TaskListener {
    @Bean("schedulerFactoryBean")
    public SchedulerFactoryBean appSchedulerFactoryBean(/*@Qualifier("dataSource") DataSource dataSource,*/
                                                        ApplicationContext applicationContext) throws IOException {
        return buildScheduler(/*dataSource, */applicationContext);
    }

    private Properties quartzProperties() throws IOException {
        var propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    private SchedulerFactoryBean buildScheduler(/*DataSource dataSource,*/ ApplicationContext applicationContext) throws IOException {
        var schedulerFactoryBean = new SchedulerFactoryBean();

        var jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);

        schedulerFactoryBean.setJobFactory(jobFactory);
        //schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setQuartzProperties(quartzProperties());

        return schedulerFactoryBean;
    }
}
