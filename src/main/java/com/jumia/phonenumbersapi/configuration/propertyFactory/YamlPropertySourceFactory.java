package com.jumia.phonenumbersapi.configuration.propertyFactory;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * YamlPropertySourceFactory is a PropertySourceFactory that allows us to parses and read config from Yaml files
 * using the @PropertySource, like this @PropertySource(value = "classpath:userConfig.yaml", factory = YamlPropertySourceFactory.class)
 * as the default behavior of @PropertySource is to only read config by parse .properties files
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(resource.getResource());
        Properties properties = factoryBean.getObject();
        return new PropertiesPropertySource(resource.getResource().getFilename(), properties);
    }
}
