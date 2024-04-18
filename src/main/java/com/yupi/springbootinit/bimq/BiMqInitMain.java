package com.yupi.springbootinit.bimq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */
public class BiMqInitMain {
    public static void main(String[] args) {
        try{
            ConnectionFactory factory  = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(BiMqConstant.BI_EXCHANGE_NAME,"direct");
            String queueName = BiMqConstant.BI_QUEUE_NAME;
            channel.queueDeclare(queueName,true,false,false,null);
            channel.queueBind(queueName,BiMqConstant.BI_EXCHANGE_NAME,BiMqConstant.BI_ROUTING_KEY);
        }catch (Exception e){

        }
    }
}
