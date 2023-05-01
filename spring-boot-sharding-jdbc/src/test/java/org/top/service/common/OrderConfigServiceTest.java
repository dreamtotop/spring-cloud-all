package org.top.service.common;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.top.model.OrderConfig;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderConfigServiceTest {

    @Resource
    private OrderConfigService orderConfigService;

    @Test
    void getOrderConfigById() {
        OrderConfig orderConfig = orderConfigService.getOrderConfigById(1L);
        System.out.println(orderConfig);
    }

    @Test
    void addOrderConfig() {
        OrderConfig orderConfig = new OrderConfig();
        orderConfig.setPayTimeout(200L);
        orderConfigService.addOrderConfig(orderConfig);
    }
}