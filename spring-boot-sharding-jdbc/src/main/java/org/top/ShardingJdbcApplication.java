package org.top;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 参数校验， vo转换， 字段脱敏， 统一异常处理， 分库分表（不同的分表算法）， 多数据源
 */
@SpringBootApplication
@MapperScan(basePackages = "org.top.mapper")
public class ShardingJdbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShardingJdbcApplication.class, args);
    }
}
