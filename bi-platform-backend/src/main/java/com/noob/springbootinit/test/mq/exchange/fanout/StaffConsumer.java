package com.noob.springbootinit.test.mq.fanout;

import com.rabbitmq.client.*;

/**
 * @ClassName StaffConsumer
 * @Description TODO
 * @Author holic-x
 * @Date 2024 2024/4/20 12:50
 */
public class StaffConsumer {

    private static final String EXCHANGE_NAME = "fanout-exchange";

    public static void main(String[] argv) throws Exception {
        // 创建工厂，设置属性
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        // 创建连接和通道
        Connection connection = factory.newConnection();

        // 1.分别创建两个通道（模拟员工小王和小李）：分别声明交换机、创建队列并绑定交换机
        Channel channelWang = connection.createChannel();
        channelWang.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        String queueWang = channelWang.queueDeclare().getQueue();
        channelWang.queueBind(queueWang, EXCHANGE_NAME, "");

        Channel channelLi = connection.createChannel();
        channelLi.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        String queueLi = channelLi.queueDeclare().getQueue();
        channelLi.queueBind(queueLi, EXCHANGE_NAME, "");

        // 打印等待信息
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 2.分别创建两个交付回调函数（模拟小王和小李回调交付）
        DeliverCallback deliverCallbackWang = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小王冲冲冲] Received '" + message + "'");
            // 模拟工作（小王工作效率高：2s处理一个任务）
            doWork(2000);
        };
        DeliverCallback deliverCallbackLi = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小李] Received '" + message + "'");
            // 模拟工作（小李工作效率低：10s处理一个任务）
            doWork(10000);
        };

        // 3.开始消费队列
        channelWang.basicConsume(queueWang, true, deliverCallbackWang, consumerTag -> { });
        channelLi.basicConsume(queueLi, true, deliverCallbackLi, consumerTag -> { });
    }

    /**
     * 模拟工作
     * @param time
     */
    private static void doWork(int time) {
        // 模拟工作
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
