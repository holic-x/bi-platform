package com.noob.springbootinit.test.mq.dlx;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName DlxConsumer
 * @Description TODO
 * @Author holic-x
 * @Date 2024 2024/4/20 14:09
 */
public class DlxConsumer {

    // 定义交换机名称
    private static final String WORK_EXCHANGE_NAME = "work-direct-exchange";

    // 定义死信交换机名称
    private static final String DEAD_EXCHANGE_NAME = "dead-direct-exchange";

    public static void main(String[] argv) throws Exception {
        // 创建工厂设置属性
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(WORK_EXCHANGE_NAME, "direct");

        // ------ 老板死信队列 ------
        // 创建用于指定死信队列的参数的Map对象
        Map<String,Object> argsBoss = new HashMap<String, Object>();
        // 将队列绑定到指定的交换机，设置死信队列参数
        argsBoss.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        // 指定死信要转发到老板队列
        argsBoss.put("x-dead-letter-routing-key", "boss");

        // 创建新的小狗队列，将其绑定到业务交换机
        String queue1 = "queue_xiaogou";
        // 声明队列：设置队列为持久化的、非独占的、非自动删除的
        channel.queueDeclare(queue1, true, false, false, argsBoss);
        // 将队列绑定到指定的交换机上，指定绑定的路由键为xiaomao
        channel.queueBind(queue1, WORK_EXCHANGE_NAME, "xiaogou");


        // ---- 外包死信队列 ------
        // 创建用于指定死信队列的参数的Map对象
        Map<String,Object> argsOd = new HashMap<String, Object>();
        // 将队列绑定到指定的交换机，设置死信队列参数
        argsOd.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        // 指定死信要转发到外包队列
        argsOd.put("x-dead-letter-routing-key", "od");

        // 创建队列，随机分配一个队列名称，并绑定到xiaogou路由键
        String queue2 = "queue_xiaomao";
        // 声明队列：设置队列为持久化的、非独占的、非自动删除的
        channel.queueDeclare(queue2, true, false, false, argsOd);
        // 将队列绑定到指定的交换机上，指定绑定的路由键为xiaogou
        channel.queueBind(queue2, WORK_EXCHANGE_NAME, "xiaomao");

        // 打印等待消息
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 分别创建两个交付回调函数（模拟小狗和小猫回调交付）
        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            // 拒绝消息，不重新将消息放回队列，只拒绝当前消息
            channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
            System.out.println(" [小狗冲冲冲] Received '" + message + "' with routing key '" + delivery.getEnvelope().getRoutingKey() + "'");
        };
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            // 拒绝消息，不重新将消息放回队列，只拒绝当前消息
            channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
            System.out.println(" [小猫] Received '" + message + "' with routing key '" + delivery.getEnvelope().getRoutingKey() + "'");
        };

        // 消费指定队列消息，设置自动确认消息已被消费
        channel.basicConsume(queue1, false, deliverCallback1, consumerTag -> { });
        channel.basicConsume(queue2, false, deliverCallback2, consumerTag -> { });
    }
}