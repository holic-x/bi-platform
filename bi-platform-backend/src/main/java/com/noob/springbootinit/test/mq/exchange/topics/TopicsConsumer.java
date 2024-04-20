package com.noob.springbootinit.test.mq.exchange.topics;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * @ClassName TopicsConsumer
 * @Description TODO
 * @Author holic-x
 * @Date 2024 2024/4/20 14:43
 */
public class TopicsConsumer {

    private static final String EXCHANGE_NAME = "topics-exchange";

    public static void main(String[] argv) throws Exception {
        // 创建工厂设置属性
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        // 创建前端队列
        String queue1 = "frontend";
        // 声明队列：设置队列为持久化的、非独占的、非自动删除的
        channel.queueDeclare(queue1, true, false, false, null);
        // 将队列绑定到指定的交换机上，指定绑定的路由键为xiaowang
        channel.queueBind(queue1, EXCHANGE_NAME, "#.前端.#");

        // 创建后端队列
        String queue2 = "backend";
        // 声明队列：设置队列为持久化的、非独占的、非自动删除的
        channel.queueDeclare(queue2, true, false, false, null);
        // 将队列绑定到指定的交换机上，指定绑定的路由键为xiaoli
        channel.queueBind(queue2, EXCHANGE_NAME, "#.后端.#");

        // 创建产品队列
        String queue3 = "product";
        // 声明队列：设置队列为持久化的、非独占的、非自动删除的
        channel.queueDeclare(queue3, true, false, false, null);
        // 将队列绑定到指定的交换机上，指定绑定的路由键为xiaoli
        channel.queueBind(queue3, EXCHANGE_NAME, "#.产品.#");

        // 打印等待消息
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 分别创建交付回调函数（不同员工接收处理）
        DeliverCallback deliverCallbackA = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小A] Received '" + message + "' with routing key '" + delivery.getEnvelope().getRoutingKey() + "'");
        };
        DeliverCallback deliverCallbackB = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小B] Received '" + message + "' with routing key '" + delivery.getEnvelope().getRoutingKey() + "'");
        };
        DeliverCallback deliverCallbackC = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小C] Received '" + message + "' with routing key '" + delivery.getEnvelope().getRoutingKey() + "'");
        };

        // 消费指定队列消息，设置自动确认消息已被消费
        channel.basicConsume(queue1, true, deliverCallbackA, consumerTag -> { });
        channel.basicConsume(queue2, true, deliverCallbackB, consumerTag -> { });
        channel.basicConsume(queue3, true, deliverCallbackC, consumerTag -> { });
    }
}
