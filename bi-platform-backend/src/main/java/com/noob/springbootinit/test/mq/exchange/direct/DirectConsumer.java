package com.noob.springbootinit.test.mq.direct;
import com.rabbitmq.client.*;

/**
 * @ClassName DirectConsumer
 * @Description TODO
 * @Author holic-x
 * @Date 2024 2024/4/20 14:09
 */
public class DirectConsumer {

    // 定义交换机名称
    private static final String EXCHANGE_NAME = "direct-exchange";

    public static void main(String[] argv) throws Exception {
        // 创建工厂设置属性
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        // 创建队列，随机分配一个队列名称，并绑定到xiaowang路由键
        String queueWang = "queue_xiaowang";
        // 声明队列：设置队列为持久化的、非独占的、非自动删除的
        channel.queueDeclare(queueWang, true, false, false, null);
        // 将队列绑定到指定的交换机上，指定绑定的路由键为xiaowang
        channel.queueBind(queueWang, EXCHANGE_NAME, "xiaowang");

        // 创建队列，随机分配一个队列名称，并绑定到xiaoli路由键
        String queueLi = "queue_xiaoli";
        // 声明队列：设置队列为持久化的、非独占的、非自动删除的
        channel.queueDeclare(queueLi, true, false, false, null);
        // 将队列绑定到指定的交换机上，指定绑定的路由键为xiaoli
        channel.queueBind(queueLi, EXCHANGE_NAME, "xiaoli");

        // 打印等待消息
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");


        // 分别创建两个交付回调函数（模拟小王和小李回调交付）
        DeliverCallback deliverCallbackWang = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小王冲冲冲] Received '" + message + "' with routing key '" + delivery.getEnvelope().getRoutingKey() + "'");
        };
        DeliverCallback deliverCallbackLi = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小李] Received '" + message + "' with routing key '" + delivery.getEnvelope().getRoutingKey() + "'");
        };

        // 消费指定队列消息，设置自动确认消息已被消费
        channel.basicConsume(queueWang, true, deliverCallbackWang, consumerTag -> { });
        channel.basicConsume(queueLi, true, deliverCallbackLi, consumerTag -> { });
    }
}