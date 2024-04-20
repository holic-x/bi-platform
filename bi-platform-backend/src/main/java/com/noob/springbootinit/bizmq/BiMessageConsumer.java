package com.noob.springbootinit.bizmq;

import com.noob.springbootinit.common.ErrorCode;
import com.noob.springbootinit.constant.CommonConstant;
import com.noob.springbootinit.exception.BusinessException;
import com.noob.springbootinit.exception.ThrowUtils;
import com.noob.springbootinit.manager.AiManager;
import com.noob.springbootinit.model.entity.Chart;
import com.noob.springbootinit.service.ChartService;
import com.noob.springbootinit.utils.ExcelUtils;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;


/**
 * @ClassName BiMessageConsumer
 * @Description TODO
 * @Author holic-x
 * @Date 2024 2024/4/20 17:38
 */
// 标记该类为一个组件，让Spring能够扫描并将其纳入管理
@Component
// 生成日志记录器
@Slf4j
public class BiMessageConsumer {


    @Resource
    private AiManager aiManager;

    @Resource
    private ChartService chartService;

    /**
     * 接收消息的方法：指定程序监听的消息队列和确认机制
     *
     * @param message
     * @param channel
     * @param deliveryTag
     */
    // SneakyThrows注解简化异常处理
    @SneakyThrows
    // RabbitListener注解设定要监听的队列名称、设置消息的确认机制
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    // @Header(AmqpHeaders.DELIVERY_TAG) 方法注解，从消息头中获取投递标签deliveryTag（RabbitMQ中每条消息都会被分配一个唯一的投递标签，用于标识该消息在通道中的投递状态和顺序）
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        // --------------- start 业务逻辑处理 ------------------
        // 2.1 修改图表任务状态为执行中
        Chart updatedChart = new Chart();
        // 接收消息参数
        long chartId = Long.parseLong(message);
        updatedChart.setId(chartId);
        updatedChart.setStatus("running");
        boolean updatedOp = chartService.updateById(updatedChart);
        if (!updatedOp) {
            handleChartUpdateError(chartId, "更新图表【执行中】状态失败");
            return;
        }
        // 2.2 直接调用AI接口
        // 根据chartId获取到chart图表信息
        Chart findChart = chartService.getById(chartId);
        ThrowUtils.throwIf(findChart == null, ErrorCode.NOT_FOUND_ERROR, "当前指定chartId关联图表信息不存在");

        String result = aiManager.doChat(CommonConstant.BI_MODEL_ID, buildUserInput(findChart));
        // 此处分隔符以设定为参考
        String[] splits = result.split("【【【【【"); // 【【【【【
        if (splits.length < 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();

        // 2.3 AI结果调用成功后，更新任务状态，并将AI结果进行封装
        Chart updatedChartResult = new Chart();
        updatedChartResult.setId(chartId);
        updatedChartResult.setGenChart(genChart);
        updatedChartResult.setGenResult(genResult);
        updatedChartResult.setStatus("succeed");
        boolean updatedResOp = chartService.updateById(updatedChartResult);
        if (!updatedResOp) {
            handleChartUpdateError(chartId, "更新图表【成功】状态失败");
        }
        // --------------- end 业务逻辑处理 ------------------

        // 使用日志记录器打印接收到的消息内容
        log.info("receiveMessage message = {}", message);
        // 手动确认消息接收，向RabbitMQ发送确认消息
        channel.basicAck(deliveryTag, false);
    }

    private String buildUserInput(Chart chart) {

        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        // 拼接分析目标
        String userGoal = chart.getGoal();
        if (StringUtils.isNotBlank(chart.getChartType())) {
            userGoal += "，请使用" + chart.getChartType();
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        // 压缩后的数据
        userInput.append(chart.getChartData()).append("\n");

        return userInput.toString();
    }

    // 此处代码设定效果等同于：channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });


    /**
     * 处理更新图表执行中状态失败方法
     *
     * @param chartId
     * @param execMessage
     */
    private void handleChartUpdateError(Long chartId, String execMessage) {
        Chart updatedChartResult = new Chart();
        updatedChartResult.setId(chartId);
        updatedChartResult.setStatus("failed");
        updatedChartResult.setExecMessage(execMessage);
        boolean updatedResOp = chartService.updateById(updatedChartResult);
        if (!updatedResOp) {
            log.error("更新图表【失败】状态失败" + chartId + "," + execMessage);
        }
    }

}