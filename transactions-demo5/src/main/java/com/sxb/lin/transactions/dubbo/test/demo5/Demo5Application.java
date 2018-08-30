package com.sxb.lin.transactions.dubbo.test.demo5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement(order = Ordered.LOWEST_PRECEDENCE - 1)
@SpringBootApplication(scanBasePackages = "com.sxb")
public class Demo5Application {

	public static void main(String[] args) {
        SpringApplication.run(Demo5Application.class, args);
    }
	
}

