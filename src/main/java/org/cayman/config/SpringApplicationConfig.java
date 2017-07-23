package org.cayman.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.CharacterEncodingFilter;


@SpringBootApplication
@EnableScheduling
@Import({MvcConfiguration.class})
@ComponentScan(value={"org.cayman.google.**", "org.cayman.service.**", "org.cayman.utils.**", "org.cayman.web.**"})
@Slf4j
public class SpringApplicationConfig {


    public static void main(String[] args) throws Exception{
        SpringApplication.run(SpringApplicationConfig.class, args);
    }


    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();

        String applicationProp = System.getProperty("application.properties");
        Resource applicationRes;
        if (applicationProp == null || applicationProp.isEmpty()) {
            applicationRes = new ClassPathResource("application.properties");
            log.info("Used default config for application properties");
        } else {
            applicationRes = new PathResource(applicationProp);
            log.info("Application properties path is " + applicationProp);
        }

        propertySourcesPlaceholderConfigurer.setLocations(applicationRes);
        propertySourcesPlaceholderConfigurer.setIgnoreResourceNotFound(false);
        return propertySourcesPlaceholderConfigurer;
    }


    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setForceEncoding(true);
        characterEncodingFilter.setEncoding("UTF-8");
        registrationBean.setFilter(characterEncodingFilter);
        return registrationBean;
    }
}
