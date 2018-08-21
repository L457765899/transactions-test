package com.sxb.lin.transactions.dubbo.test.demo4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.core.Ordered;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJms
@EnableTransactionManagement(order = Ordered.LOWEST_PRECEDENCE - 1)
@SpringBootApplication(scanBasePackages = "com.sxb",exclude = JtaAutoConfiguration.class)
public class Demo4Application {

	public static void main(String[] args) {
        SpringApplication.run(Demo4Application.class, args);
    }
	
}

