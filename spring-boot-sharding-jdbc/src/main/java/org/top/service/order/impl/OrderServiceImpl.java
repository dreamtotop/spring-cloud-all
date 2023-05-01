package org.top.service.order.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.top.mapper.OrderConfigMapper;
import org.top.mapper.OrderMapper;
import org.top.model.Order;
import org.top.model.OrderConfig;
import org.top.service.order.OrderService;

import javax.annotation.Resource;
import java.util.List;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderConfigMapper orderConfigMapper;

    @Override
    public int createOrder(Order order) {
        return orderMapper.createOrder(order);
    }

    @Override
    public List<Order> getOrders() {
        return orderMapper.getOrders();
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderMapper.getOrdersByUserId(userId);
    }

    @Override
    public int updateOrder(Order order) {
        return 0;
    }

    @Override
    public int deleteOrdersById(Long id) {
        return 0;
    }

    @Override
    @Transactional
    public void addOrderAndConfig(Order order) {
        orderMapper.createOrder(order);
        OrderConfig orderConfig = new OrderConfig();
        orderConfig.setPayTimeout(200L);
        orderConfigMapper.addOrderConfig(orderConfig);
        //throw new RuntimeException("模拟异常");
    }


}
