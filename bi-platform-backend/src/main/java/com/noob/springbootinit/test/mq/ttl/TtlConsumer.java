package com.noob.springbootinit.test.mq.ttl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName TtlConsumer
 * @Description
 * @Author holic-x
 * @Date 2024 2024/4/20 11:20
 */
public class TtlConsumer {

    // 定义静态常量（hello）：表示正在监听的队列名称
    private final static String QUEUE_NAME = "ttl_queue";

    public static void main(String[] argv) throws Exception {
        // 创建连接工厂，设置连接属性
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        // 从工厂获取一个姓的连接
        Connection connection = factory.newConnection();
        // 创建连接通道
        Channel channel = connection.createChannel();


        // 创建队列，指定消息过期参数
        Map<String,Object> args = new HashMap<>();
        // 设置消息过期时间为5s
        args.put("x-message-ttl", 5000);
        channel.queueDeclare(QUEUE_NAME, false, false, false, args);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 处理消息：使用deliverCallback处理接收到的消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
        };
        // 测试1：在指定频道上消费队列中的消息，接收到的消息会传递给deliverCallback处理，持续阻塞
//        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });

        // 测试2：测试不自动确认的情况下消息是否会丢失
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });

    }
}
