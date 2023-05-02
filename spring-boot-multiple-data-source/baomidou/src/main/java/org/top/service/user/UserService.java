package org.top.service.user;

import org.springframework.stereotype.Service;
import org.top.mapper.UserMapper;
import org.top.model.User;

import javax.annotation.Resource;

@Service("userService")
public class UserService {

    @Resource
    private UserMapper userMapper;

    public void addUser(User user){
        userMapper.addUser(user);
    }

    public User selectUserById(Long id){
        return userMapper.selectUserById(id);
    }
}
