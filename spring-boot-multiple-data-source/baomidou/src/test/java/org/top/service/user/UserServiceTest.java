package org.top.service.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.top.model.User;

import javax.annotation.Resource;

@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void addUser() {
        User user = new User();
        user.setUserName("jack");
        user.setPassword("123456");
        userService.addUser(user);
    }

    @Test
    void selectUserById() {
        User user = userService.selectUserById(1L);
        System.out.println(user);
    }
}