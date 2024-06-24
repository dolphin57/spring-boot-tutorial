package io.dolphin.uid;

import io.dolphin.uid.exception.InitException;
import io.dolphin.uid.service.SegmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

/**
 * @author dolphin
 * @date 2024年05月16日 10:27
 * @description
 */
@Configuration
@EnableConfigurationProperties(UIDGeneratorProperties.class)
public class UIDGeneratorStarterAutoConfigure {
    private Logger logger = LoggerFactory.getLogger(UIDGeneratorStarterAutoConfigure.class);

    @Autowired
    private UIDGeneratorProperties properties;

    @Bean
    public SegmentService initSegmentStarter() throws SQLException, InitException {
        if (properties != null && properties.getSegment() != null && properties.getSegment().isEnable()) {
            return new SegmentService(properties.getSegment().getUrl(), properties.getSegment().getUsername(), properties.getSegment().getPassword());
        }
        logger.warn("init leaf segment ignore properties is {}", properties);
        return null;
    }
}
