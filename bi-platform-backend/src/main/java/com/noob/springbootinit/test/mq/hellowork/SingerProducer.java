package com.noob.springbootinit.test.mq.hellowork;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName Send
 * @Description 定义SingerProducer：实现消息发送功能
 * @Author holic-x
 * @Date 2024 2024/4/20 11:08
 */


public class SingerProducer {

    // 定义静态常量（hello）：表示向名为hello的队列发送信息
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        // 创建ConnectionFactory对象，用于创建到RabbitMQ服务器的连接（设置factory的相关属性）
        ConnectionFactory factory = new ConnectionFactory();
        // 设置连接主机(如果修改了端口号、设定了用户名密码，则此处相应要设置，如果没有额外变动则默认即可)
        factory.setHost("localhost");
        // 创建连接：实现与RabbitMQ服务进行交互
        try (Connection connection = factory.newConnection();
             // 在通道声明一个队列，指定队列名为hello
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            // 构建要发送的消息内容
            String message = "Hello World!";
            // 使用channel.basicPublish方法发布消息到指定队列
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}