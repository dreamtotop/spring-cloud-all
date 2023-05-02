package org.top.mapper.order;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Param;
import org.top.constant.DBConstants;
import org.top.model.Order;

@DS(DBConstants.DATASOURCE_ORDERS)
public interface OrderMapper {

    void addOrder(Order order);

    Order selectById(@Param("id") Long id);
}
