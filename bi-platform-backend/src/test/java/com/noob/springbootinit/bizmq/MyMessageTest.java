package com.noob.springbootinit.bizmq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @ClassName MyMessageTest
 * @Description TODO
 * @Author holic-x
 * @Date 2024 2024/4/20 17:56
 */
@SpringBootTest
class MyMessageTest {

    @Resource
    private MyMessageProducer myMessageProducer;

    @Resource
    private MyMessageConsumer myMessageConsumer;

    @Test
    void sendMessage() {
        myMessageProducer.sendMessage("code_exchange","my_routingKey","hello world");
    }

}