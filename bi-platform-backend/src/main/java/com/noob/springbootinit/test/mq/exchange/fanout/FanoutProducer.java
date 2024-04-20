package com.noob.springbootinit.test.mq.fanout;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

/**
 * @ClassName FanoutProducer
 * @Description TODO
 * @Author holic-x
 * @Date 2024 2024/4/20 12:50
 */
public class FanoutProducer {
    // 定义要使用的交换机名称
    private static final String EXCHANGE_NAME = "fanout-exchange";

    public static void main(String[] argv) throws Exception {
        // 创建连接工厂，设置属性
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        // 创建连接、通道
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            // 声明交换机类型
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

            // 创建Scanner模拟生产（通过控制台输入生产消息）
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String message = scanner.next();
                // 将消息发送到指定的交换机（fanout交换机），不指定路由键（空字符串）
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
                // 打印发送的消息内容
                System.out.println(" [x] Sent '" + message + "'");
            }
        }
    }
}
