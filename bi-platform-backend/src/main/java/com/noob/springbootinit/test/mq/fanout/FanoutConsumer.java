package com.noob.springbootinit.test.mq.fanout;

import com.rabbitmq.client.*;

/**
 * @ClassName FanoutConsumer
 * @Description TODO
 * @Author holic-x
 * @Date 2024 2024/4/20 12:50
 */
public class FanoutConsumer {

    private static final String EXCHANGE_NAME = "fanout-exchange";

    public static void main(String[] argv) throws Exception {
        // 创建工厂，设置属性
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        // 创建连接和通道
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        String queueName = channel.queueDeclare().getQueue();
        // 绑定交换机
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        // 回调处理
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}
