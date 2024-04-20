package com.noob.springbootinit.test.mq.workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;

/**
 * @ClassName MultiProducer
 * @Description TODO
 * @Author holic-x
 * @Date 2024 2024/4/20 11:36
 */
public class MultiProducer {
    // 定义静态常量（hello）：表示向名为hello的队列发送信息
    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] argv) throws Exception {
        // 创建ConnectionFactory对象，用于创建到RabbitMQ服务器的连接（设置factory的相关属性）
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        // 创建连接：实现与RabbitMQ服务进行交互
        try (Connection connection = factory.newConnection();
             // 创建新频道：声明队列参数（队列名称、是否持久化等）
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

            // 创建输入扫描器，读取控制台输入内容发送
            Scanner console = new Scanner(System.in);
            while (console.hasNext()) {
                // 读取控制台文本
                String message = console.next();
                // 发布消息队列，设置消息持久化
                channel.basicPublish("", TASK_QUEUE_NAME,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'");
            }
        }
    }
}
