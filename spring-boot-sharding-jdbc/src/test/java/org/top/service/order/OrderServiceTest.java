package org.top.service.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.top.model.Order;

import javax.annotation.Resource;
import java.util.List;


@SpringBootTest
public class OrderServiceTest {

    @Resource
    private OrderService orderService;

    @Test
    void createOrder() {
        Order order = new Order();
        for(int i = 1; i<=8 ; i++) {
            order.setUserId((long) i);
            orderService.createOrder(order);
        }
    }

    @Test
    void getOrders() {
        List<Order> orders = orderService.getOrders();
        System.out.println(orders.size());
    }

    @Test
    void getOrdersByUserId() {
        List<Order> orders = orderService.getOrdersByUserId(1L);
        System.out.println(orders);
    }

    @Test
    void addOrderAndConfig(){
        Order order = new Order();
        order.setUserId(10L);
        orderService.addOrderAndConfig(order);
    }
}