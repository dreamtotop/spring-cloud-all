package org.top;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan(basePackages = "org.top.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class BaomidouDataSourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaomidouDataSourceApplication.class, args);
    }
}
