package com.noob.springbootinit.test.mq.exchange.topics;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

/**
 * @ClassName TopicsProducer
 * @Description TODO
 * @Author holic-x
 * @Date 2024 2024/4/20 14:42
 */
public class TopicsProducer {

    // 定义交换机名称
    private static final String EXCHANGE_NAME = "topics-exchange";

    public static void main(String[] argv) throws Exception {
        // 创建工厂、设置属性
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        // 创建连接、通道
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            // 获取严重程度（路由键）和消息内容（模拟从控制台输入）
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
                // 发布消息到交换机
                channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '"  + "':'" + message + "'  with routingKey: " + routingKey);
            }
        }
    }
}
