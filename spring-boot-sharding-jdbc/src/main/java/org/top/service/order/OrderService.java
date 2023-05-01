package org.top.service.order;

import org.top.model.Order;

import java.util.List;

public interface OrderService {

    int createOrder(Order order);

    List<Order> getOrders();

    List<Order> getOrdersByUserId(Long userId);

    int updateOrder(Order order);

    int deleteOrdersById(Long id);


    void addOrderAndConfig(Order order);

}
