package com.snail.globalid;

import com.snail.globalid.buffer.AtomicLongBufferRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GlobalidApplicationTests {
    @Autowired
    RedisConnectionFactory connectionFactory;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Resource
    AtomicLongBufferRepository repository;
    @Test
    public void notNullTest() throws Exception {
        System.out.println(repository.incrementAndGet("sss"));
    }
}
