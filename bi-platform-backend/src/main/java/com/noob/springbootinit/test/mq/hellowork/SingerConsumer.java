package com.noob.springbootinit.test.mq.hellowork;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName SingerConsumer
 * @Description 定义SingerConsumer：实现消息消费功能
 * @Author holic-x
 * @Date 2024 2024/4/20 11:20
 */
public class SingerConsumer {

    // 定义静态常量（hello）：表示正在监听的队列名称
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        // 创建连接工厂，设置连接属性
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        // 从工厂获取一个姓的连接
        Connection connection = factory.newConnection();
        // 创建连接通道
        Channel channel = connection.createChannel();
        // 创建队列，在该频道中声明正在监听的队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 处理消息：使用deliverCallback处理接收到的消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
        };
        // 在指定频道上消费队列中的消息，接收到的消息会传递给deliverCallback处理，持续阻塞
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}
