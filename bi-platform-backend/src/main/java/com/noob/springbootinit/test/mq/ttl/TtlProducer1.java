package com.noob.springbootinit.test.mq.ttl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName TtlProducer1
 * @Description
 * @Author holic-x
 * @Date 2024 2024/4/20 11:08
 */


public class TtlProducer1 {
    public static void main(String[] argv) throws Exception {
        // 创建ConnectionFactory对象，用于创建到RabbitMQ服务器的连接（设置factory的相关属性）
        ConnectionFactory factory = new ConnectionFactory();
        // 设置连接主机(如果修改了端口号、设定了用户名密码，则此处相应要设置，如果没有额外变动则默认即可)
        factory.setHost("localhost");
        // 创建连接：实现与RabbitMQ服务进行交互
        try (Connection connection = factory.newConnection();
             // 创建通道
             Channel channel = connection.createChannel()) {
            // 构建要发送的消息内容
            String message = "test ttl message";

            // 给消息指定过期时间
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    // 设置消息过期时间（1000ms）
                    .expiration("10000")
                            .build();
            // 使用channel.basicPublish方法发布消息到指定队列(发布到my-exchange交换机、routing-key路由键)
            channel.basicPublish("my-exchange", "routing-key", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}