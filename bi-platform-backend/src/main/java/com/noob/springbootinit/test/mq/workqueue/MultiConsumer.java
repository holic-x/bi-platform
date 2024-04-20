package com.noob.springbootinit.test.mq.workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;


/**
 * @ClassName MultiConsumer
 * @Description TODO
 * @Author holic-x
 * @Date 2024 2024/4/20 11:37
 */
public class MultiConsumer {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] argv) throws Exception {
//        publishMessage01();
        publishMessage02();
    }

    /**
     * 提高并发处理能力：
     *
     * @throws Exception
     */
    private static void publishMessage02() throws Exception {
        // 创建连接工厂，设置连接信息
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        // 从工厂获取连接
        final Connection connection = factory.newConnection();
        // 将通道创建的代码通过for封装
        for (int i = 0; i < 2; i++) {
            // 创建新通道
            final Channel channel = connection.createChannel();
            // 声明一个队列：设置队列属性：队列名称、持久化、非排他、非自动删除、其他参数；如果队列不存在则创建
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            // 设计预取计数为1，RabbitMQ给消费者新消息之前等待之前的消息被确认
            channel.basicQos(1);

            int finalI = i;

            // 创建消息回调函数，接收信息
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");

                try {
                    System.out.println(" [x] Received '" + "编号：" + finalI + "- 接收消息：" + message + "'");
                    // 模拟处理工作（设定模拟处理花费时间，计算机处理能力有限，接收一条消息之后20s后再接收下一条消息）
                    Thread.sleep(20000);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    System.out.println(" [x] Done");
                    // 手动发送应答，告诉RabbitMQ消息已经被处理
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            // 开始消费信息，传入队列名称、是否自动确认、投递回调、消费者取消回调
            channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });
        }
    }

    /**
     * 发布消息（计算机处理能力有限：消费完一个消息才能继续消费下一个消息）
     *
     * @throws Exception
     */
    private static void publishMessage01() throws Exception {
        // 创建连接工厂，设置连接信息
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        // 从工厂获取连接
        final Connection connection = factory.newConnection();
        // 创建新通道
        final Channel channel = connection.createChannel();
        // 声明一个队列：设置队列属性：队列名称、持久化、非排他、非自动删除、其他参数；如果队列不存在则创建
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        // 设计预取计数为1，RabbitMQ给消费者新消息之前等待之前的消息被确认
//        channel.basicQos(1);

        // 创建消息回调函数，接收信息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            try {
                System.out.println(" [x] Received '" + message + "'");
                // 模拟处理工作（设定模拟处理花费时间，计算机处理能力有限，接收一条消息之后20s后再接收下一条消息）
//                doWork(message);
                Thread.sleep(20000);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                System.out.println(" [x] Done");
                // 手动发送应答，告诉RabbitMQ消息已经被处理
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };
        // 开始消费信息，传入队列名称、是否自动确认、投递回调、消费者取消回调
        channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
        });
    }

    private static void doWork(String task) {
        for (char ch : task.toCharArray()) {
            if (ch == '.') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

