package org.top.service.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.top.model.Order;

import javax.annotation.Resource;



@SpringBootTest
public class OrderServiceTest {

    @Resource
    private OrderService orderService;


    @Test
    void addOrder() {
        Order order = new Order();
        order.setUserId(1L);
        orderService.addOrder(order);
    }

    @Test
    void selectById() {
        Order order = orderService.selectById(1L);
        System.out.println(order);
    }

    @Test
    void method01(){
        orderService.method01();
    }

    @Test
    void method02(){
        orderService.method02();
    }

    @Test
    void method03(){
        orderService.method03();
    }

    @Test
    void method04(){
        orderService.method04();
    }
}