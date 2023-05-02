package org.top.service.order;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.top.constant.DBConstants;
import org.top.mapper.UserMapper;
import org.top.mapper.order.OrderMapper;
import org.top.model.Order;
import org.top.model.User;

import javax.annotation.Resource;

@Service("orderService")
public class OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;

    public void addOrder(Order order){
        orderMapper.addOrder(order);
    }

    public Order selectById(Long id){
        return orderMapper.selectById(id);
    }

    //   ********************   测试多数据源事务切换问题

    // 获取代理对象
    private OrderService self() {
        return (OrderService) AopContext.currentProxy();
    }

    /***
     * 不存在事务
     */
    public void method01(){
        Order order = orderMapper.selectById(1L);
        System.out.println(order);
        User user = userMapper.selectUserById(1L);
        System.out.println(user);
    }

    /**
     * 开启事务   Table 'test_users.orders' doesn't exist
     */
    @Transactional
    @DS(DBConstants.DATASOURCE_USERS)
    public void method02() {
        // 查询用户
        User user = userMapper.selectUserById(1L);
        System.out.println(user);
        // 查询订单
        Order order = orderMapper.selectById(1L);
        System.out.println(order);
    }

    /**
     *  1. 因为 this 不是代理对象，所以 #method031() 和 #method032() 方法上的 @Transactional 直接没有作用，
     *  Spring 事务根本没有生效。所以，最终结果和场景一是等价的。
     *
     *  2. 处于不同的事务中（互相不影响）, 容易出现多个事务数据状态不一致
     */
    public void method03(){
        self().method031();
        self().method032();

//        this.method031();
//        this.method032();
    }

    @Transactional
    @DS(DBConstants.DATASOURCE_USERS)
    public void method031(){
        // 查询用户
        User user = new User();
        user.setUserName("marry");
        user.setPassword("456789");
        userMapper.addUser(user);
        User userDB = userMapper.selectUserById(user.getId());
        System.out.println(userDB);
    }

    @Transactional
    @DS(DBConstants.DATASOURCE_ORDERS)
    public void method032(){
        // 查询订单
        Order order = new Order();
        order.setUserId(2L);
        orderMapper.addOrder(order);
        Order orderDB = orderMapper.selectById(order.getId());
        System.out.println(orderDB);
        //throw new RuntimeException("模拟异常回滚");
    }


    /***
     * 处于同一个事务中   Table 'test_orders.users' doesn't exist
     *
     * 解决方案： @Transactional(propagation = Propagation.REQUIRES_NEW)
     *
     * 异常回归全部回滚
     */
    @Transactional
    @DS(DBConstants.DATASOURCE_ORDERS)
    public void method04(){
        Order order = new Order();
        order.setUserId(3L);
        orderMapper.addOrder(order);
        Order orderDB = orderMapper.selectById(order.getId());
        System.out.println(orderDB);

        self().method042();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DS(DBConstants.DATASOURCE_USERS)
    public void method042(){
        User user = new User();
        user.setUserName("marry");
        user.setPassword("456789");
        userMapper.addUser(user);
        User userDB = userMapper.selectUserById(user.getId());
        System.out.println(userDB);
        //throw new RuntimeException("模拟异常回滚");
    }


}
