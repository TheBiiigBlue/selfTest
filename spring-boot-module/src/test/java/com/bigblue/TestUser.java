package com.bigblue;


import com.bigblue.domain.User;
import com.bigblue.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @Author: TheBigBlue
 * @Description:
 * @Date: 2019/5/28
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestUser {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUser() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        userList.forEach(System.out::println);
    }

    @Test
    public void testInsert() {
        userMapper.insert(new User(2L, "zhangsan", 11, new Date()));
        List<User> userList = userMapper.selectList(null);
        userList.forEach(System.out::println);
    }
}
