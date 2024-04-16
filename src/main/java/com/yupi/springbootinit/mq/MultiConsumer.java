package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class MultiConsumer {

    private static final String TASK_QUEUE_NAME = "multi_queue";

    public static void main(String[] argv) throws Exception {
        // 建立连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();
        for (int i = 0; i < 2; i++) {
            final Channel channel = connection.createChannel();

            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            //消费者每次只能处理一个消息
            channel.basicQos(1);

            // 定义了如何处理消息
            int finalI = i;
            // 创建消息接收回调函数,以便接收消息
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                try {
                    // 处理工作
                    System.out.println(" [x] Received '" + "编号:" + finalI + ":" + message + "'");
                    // 停 20 秒，模拟机器处理能力有限
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(" [x] Done");
                    // 手动发送应答,告诉RabbitMQ消息已经被处理
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            // 开始消费消息,传入队列名称,是否自动确认,投递回调和消费者取消回调
            channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });
        }
    }
}