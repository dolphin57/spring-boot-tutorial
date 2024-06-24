package io.dolphin.libsvm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author dolphin
 * @date 2024年03月20日 10:22
 * @description
 */
@SpringBootApplication
@ComponentScan(basePackages = {"io.dolphin.libsvm.core"})
public class CreditScoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(CreditScoreApplication.class, args);
    }
}
