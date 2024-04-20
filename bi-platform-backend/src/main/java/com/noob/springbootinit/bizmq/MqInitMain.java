package com.noob.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName MqInitMain
 * @Description TODO
 * @Author holic-x
 * @Date 2024 2024/4/20 17:47
 */
// 用于创建测试程序用到的交换机和队列(只用在程序启动前执行一次)
public class MqInitMain {

    public static void main(String[] args) {

        try {
            // 创建连接工厂
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            // 创建连接
            Connection connection = factory.newConnection();
            // 创建通道
            Channel channel = connection.createChannel();
            // 定义交换机
            String EXCHANGE_NAME = "code_exchange";
            // 声明交换机，指定交换机类型为direct
            channel.exchangeDeclare(EXCHANGE_NAME,"direct");
            // 创建队列，并随机分配队列名称
            String QUEUE_NAME = "code_queue";
            // 声明队列，设置队列持久化、非独占、非自动删除、传入额外的参数为null
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);
            // 将队列绑定到交换机，指定路由键为"my_routingKey"
            channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"my_routingKey");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

    }
}
