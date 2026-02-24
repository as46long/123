package com.leyu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.leyu.mapper")
public class LeyuApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeyuApplication.class, args);
    }
}
