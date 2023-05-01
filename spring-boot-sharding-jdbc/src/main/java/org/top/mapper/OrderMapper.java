package org.top.mapper;

import org.apache.ibatis.annotations.Param;
import org.top.model.Order;

import java.util.List;

public interface OrderMapper {

    int createOrder(Order order);

    List<Order> getOrders();

    List<Order> getOrdersByUserId(@Param("userId") Long userId);

    //int updateOrder(Order order);

    //int deleteOrdersById(Long id);

}
