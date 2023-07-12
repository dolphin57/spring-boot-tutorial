package com.eric;

import com.eric.entity.User;
import com.eric.mapper.UserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpringBootMybatisPlusTest {
    @Autowired
    private UserMapper userMapper;
    @Test
    void updateUser() {
        User user = new User();
        user.setId(1);
        user.setPassword("123");
        user.setVersion(1);
        userMapper.updateById(user);
    }

    @Test
    void updateExistUser() {
        User existUser = userMapper.selectById(1);
        existUser.setPassword("123456");
        userMapper.updateById(existUser);
    }
}