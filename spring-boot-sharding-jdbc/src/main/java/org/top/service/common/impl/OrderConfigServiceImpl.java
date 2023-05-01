package org.top.service.common.impl;

import org.springframework.stereotype.Service;
import org.top.mapper.OrderConfigMapper;
import org.top.model.OrderConfig;
import org.top.service.common.OrderConfigService;

import javax.annotation.Resource;

@Service("orderConfigService")
public class OrderConfigServiceImpl implements OrderConfigService {

    @Resource
    private OrderConfigMapper orderConfigMapper;

    @Override
    public OrderConfig getOrderConfigById(Long id) {
        return orderConfigMapper.getOrderConfigById(id);
    }

    @Override
    public int addOrderConfig(OrderConfig orderConfig) {
        return orderConfigMapper.addOrderConfig(orderConfig);
    }
}
