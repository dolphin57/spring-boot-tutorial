package io.dolphin.leaf;

import com.sankuai.inf.leaf.plugin.annotation.EnableLeafServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author dolphin
 * @date 2024年04月07日 11:10
 * @description
 */
@EnableLeafServer
@SpringBootApplication
public class LeafApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeafApplication.class);
    }
}
