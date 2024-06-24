package io.dolphin;

import com.sankuai.inf.leaf.plugin.annotation.EnableLeafServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author dolphin
 * @date 2024年04月10日 15:16
 * @description
 */
@EnableLeafServer
@SpringBootApplication
public class SnowFlakeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SnowFlakeApplication.class, args);
    }
}
