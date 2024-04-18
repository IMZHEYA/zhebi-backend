package com.yupi.springbootinit.bimq;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyMessageConsumer {


    //使用@SneakyThrows注解简化异常处理
    //使得你可以在不声明抛出异常的方法中抛出受检异常，而无需捕获它们。这在一些特定情况下可能会很有用，但通常不建议频繁使用，因为它可能会破坏代码的可读性和健壮性。
    @SneakyThrows
    //使用@RabbitListener注解指定要监听的队列名称为"code_queue"，并设置消息的确认机制为手动确认
    @RabbitListener(queues = {"code_queue"},ackMode = "MANUAL")
    // // 在RabbitMQ中,每条消息都会被分配一个唯一的投递标签，用于标识该消息在通道中的投递状态和顺序。通过使用@Header(AmqpHeaders.DELIVERY_TAG)注解,可以从消息头中提取出该投递标签,并将其赋值给long deliveryTag参数。
    public void reciveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliverttag){
        log.info("receiveMessage message = {}", message);
        //手动确认消息的接收
        channel.basicAck(deliverttag,false);
    }
}
