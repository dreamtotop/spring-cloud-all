package org.top.mapper;

import org.apache.ibatis.annotations.Param;
import org.top.model.OrderConfig;

public interface OrderConfigMapper {

    OrderConfig getOrderConfigById(@Param("id") Long id);

    int addOrderConfig(OrderConfig orderConfig);
}
