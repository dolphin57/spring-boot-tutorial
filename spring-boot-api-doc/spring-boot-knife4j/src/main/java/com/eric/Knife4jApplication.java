package com.eric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Description:
 * @Author: Eric Liang
 * @Since: 2020-10-13 17:36
 */
@SpringBootApplication
public class Knife4jApplication {
    static Logger logger= LoggerFactory.getLogger(Knife4jApplication.class);

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application= SpringApplication.run(Knife4jApplication.class, args);
        Environment env = application.getEnvironment();
        String host= InetAddress.getLocalHost().getHostAddress();
        String port=env.getProperty("server.port");
        logger.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\thttp://localhost:{}\n\t" +
                        "External: \thttp://{}:{}\n\t"+
                        "Doc: \thttp://{}:{}/doc.html\n\t"+
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                host,port,
                host,port);
    }
}
