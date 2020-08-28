package com.eric.starter.config;

import com.eric.starter.roperties.CalculateProperties;
import com.eric.starter.service.CalculateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: Eric Liang
 * @Since: 2020-8-27 16:21
 */
@Configuration
@ConditionalOnClass(CalculateService.class)
@EnableConfigurationProperties(CalculateProperties.class)
public class CalculateAutoConfigure {
    @Autowired
    private CalculateProperties calculateProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "calculate", value = "enabled", havingValue = "true")
    public CalculateService calculateService() {
        return new CalculateService(calculateProperties.getScale());
    }
}
