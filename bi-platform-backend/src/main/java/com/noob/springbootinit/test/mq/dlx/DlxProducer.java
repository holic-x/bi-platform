package com.noob.springbootinit.test.mq.dlx;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.Scanner;


/**
 * @ClassName DlxProducer
 * @Description TODO
 * @Author holic-x
 * @Date 2024 2024/4/20 14:08
 */

public class DlxProducer {

    // 定义交换机名称
    private static final String WORK_EXCHANGE_NAME = "work-direct-exchange";

    // 定义死信交换机名称
    private static final String DEAD_EXCHANGE_NAME = "dead-direct-exchange";


    public static void main(String[] argv) throws Exception {
        // 创建工厂、设置属性
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        // 创建连接、通道
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            // 声明死信交换机
            channel.exchangeDeclare(DEAD_EXCHANGE_NAME, "direct");

            // 创建给老板的死信队列并绑定
            String queueBoss = "boss_dlx_queue";
            channel.queueDeclare(queueBoss, true, false, false, null);
            channel.queueBind(queueBoss, DEAD_EXCHANGE_NAME, "boss");

            // 创建给外包的死信队列
            String queueOd = "od_dlx_queue";
            channel.queueDeclare(queueOd, true, false, false, null);
            channel.queueBind(queueOd, DEAD_EXCHANGE_NAME, "od");

            // 创建用于处理老板死信队列的回调函数，当收到消息时，拒绝消息并打印消息内容
            DeliverCallback deliverCallbackBoss = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                // 拒绝消息，不重新将消息放回队列，只拒绝当前消息
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                System.out.println(" [boss-死信] Received '" + message + "' with routing key '" + delivery.getEnvelope().getRoutingKey() + "'");
            };
            // 创建用于处理外包死信队列的回调函数，当收到消息时，拒绝消息并打印消息内容
            DeliverCallback deliverCallbackOd = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                // 拒绝消息，不重新将消息放回队列，只拒绝当前消息
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                System.out.println(" [od-死信] Received '" + message + "' with routing key '" + delivery.getEnvelope().getRoutingKey() + "'");
            };

            // 消费指定队列消息
            channel.basicConsume(queueBoss, false, deliverCallbackBoss, consumerTag -> { });
            channel.basicConsume(queueOd, false, deliverCallbackOd, consumerTag -> { });

            // 模拟消息发布
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                // 读取用户输入信息（约定格式：routingKey,message）
                String userInput = scanner.nextLine();
                String[] userInputs = userInput.split(",");
                // 校验输入格式，如果满足则继续读取下一行
                if(userInputs.length<1){
                    continue;
                }
                // 获取路由键和消息内容进行封装
                String routingKey = userInputs[0];
                String message = userInputs[1];
                // 发布消息到交换机（业务交换机）
            channel.basicPublish(WORK_EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '"  + "':'" + message + "'  with routingKey: " + routingKey);
            }
        }
    }
}
