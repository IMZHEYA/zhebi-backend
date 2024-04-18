package com.yupi.springbootinit.bimq;

import cn.hutool.core.text.StrBuilder;
import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.utils.ExcelUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.RegEx;
import javax.annotation.Resource;

@Component
@Slf4j
public class BiMessageConsumer {

    @Resource
    private ChartService chartService;


    @Resource
    private AiManager aiManager;

    //使用@SneakyThrows注解简化异常处理
    //使得你可以在不声明抛出异常的方法中抛出受检异常，而无需捕获它们。这在一些特定情况下可能会很有用，但通常不建议频繁使用，因为它可能会破坏代码的可读性和健壮性。
    @SneakyThrows
    //使用@RabbitListener注解指定要监听的队列名称为"code_queue"，并设置消息的确认机制为手动确认
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME},ackMode = "MANUAL")
    // // 在RabbitMQ中,每条消息都会被分配一个唯一的投递标签，用于标识该消息在通道中的投递状态和顺序。通过使用@Header(AmqpHeaders.DELIVERY_TAG)注解,可以从消息头中提取出该投递标签,并将其赋值给long deliveryTag参数。
    public void reciveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliverttag){
        log.info("receive message = {}" + message);
        if (StringUtils.isBlank(message)) {
            //拒绝消息
            channel.basicNack(deliverttag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息为空");
        }
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if(chart == null){
            channel.basicNack(deliverttag,false,false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"图表为空");
        }
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus("running");
        boolean b = chartService.updateById(updateChart);
        if(!b){
            handleChartUpdateError(chart.getId(),"更新图表执行中状态失败");
            return;
        }
        String result = aiManager.doChat(CommonConstant.BI_MODEL_ID, getUserInput(chart));
        String[] splits = result.split("【【【【【");
        if(splits.length < 3){
            handleChartUpdateError(chart.getId(),"AI生成错误");
            return;
        }
        String genChart = splits[1];
        String genResult = splits[2];
        Chart updateChart2 = new Chart();
        updateChart2.setId(chart.getId());
        updateChart2.setStatus("succeed");
        updateChart2.setGenChart(genChart);
        updateChart2.setGenResult(genResult);
        boolean b1 = chartService.updateById(updateChart2);
        if(!b1){
            handleChartUpdateError(chart.getId(),"更新图表成功状态失败");
            return;
        }
        //确认消息
        channel.basicAck(deliverttag,false);
    }

    /**
     * 根据chart获取用户的输入
     * @param chart
     * @param
     */
    public String getUserInput(Chart chart){
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String CSVData = chart.getChartData();
        StrBuilder userInput = new StrBuilder();
        userInput.append("分析需求：").append("\n");
        String userGoal = goal;
        if(StringUtils.isNotBlank(chartType)){
            //指定了图表类型，就在目标上拼接请使用，图表类型
            userGoal += "请使用，"  + chartType;
        }
        userInput.append(CSVData).append("\n");
        return userInput.toString();
    }
    public void handleChartUpdateError(long chartId,String execMessage){
        Chart chart = new Chart();
        chart.setId(chartId);
        chart.setStatus("failed");
        chart.setExecMessage(execMessage);
        boolean b = chartService.updateById(chart);
        if(!b){
            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
        }
    }
}
