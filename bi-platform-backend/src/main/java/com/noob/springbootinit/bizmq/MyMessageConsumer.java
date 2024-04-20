package com.noob.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;


/**
 * @ClassName MyMessageConsumer
 * @Description TODO
 * @Author holic-x
 * @Date 2024 2024/4/20 17:38
 */
// 标记该类为一个组件，让Spring能够扫描并将其纳入管理
@Component
// 生成日志记录器
@Slf4j
public class MyMessageConsumer {

    /**
     * 接收消息的方法：指定程序监听的消息队列和确认机制
     * @param message
     * @param channel
     * @param deliveryTag
     */
    // SneakyThrows注解简化异常处理
    @SneakyThrows
    // RabbitListener注解设定要监听的队列名称、设置消息的确认机制
    @RabbitListener(queues = {"code_queue"}, ackMode = "MANUAL")
    // @Header(AmqpHeaders.DELIVERY_TAG) 方法注解，从消息头中获取投递标签deliveryTag（RabbitMQ中每条消息都会被分配一个唯一的投递标签，用于标识该消息在通道中的投递状态和顺序）
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        // 使用日志记录器打印接收到的消息内容
        log.info("receiveMessage message = {}", message);
        // 手动确认消息接收，向RabbitMQ发送确认消息
        channel.basicAck(deliveryTag, false);
    }

    // 此处代码设定效果等同于：channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });

}