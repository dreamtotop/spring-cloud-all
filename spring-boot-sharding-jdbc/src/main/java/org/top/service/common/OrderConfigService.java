package org.top.service.common;

import org.top.model.OrderConfig;

public interface OrderConfigService {

    OrderConfig getOrderConfigById(Long id);

    int addOrderConfig(OrderConfig orderConfig);

}
