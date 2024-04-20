package com.noob.springbootinit.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName BiMessageProducer
 * @Description TODO
 * @Author holic-x
 * @Date 2024 2024/4/20 17:30
 */
// 标记该类为一个组件，让Spring能够扫描并将其纳入管理
@Component
public class BiMessageProducer {

    // 注入RabbitTemplate
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param exchange 交换机名称：指定消息要发送到哪个交换机
     * @param routingKey 路由键：指定消息根据什么路由规则转发到对应的队列
     * @param message 消息内容：要发送的消息
     */
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY, message);
    }

}