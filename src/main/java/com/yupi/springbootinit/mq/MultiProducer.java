package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;

public class MultiProducer {
    //队列名字
  private static final String TASK_QUEUE_NAME = "multi_queue";

  public static void main(String[] argv) throws Exception {
      //建立连接
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
        //创建队列
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.nextLine();
            //在指定的交换机上发布消息到TASK_QUEUE_NAME的队列中，使用 MessageProperties.PERSISTENT_TEXT_PLAIN将消息标为持久化，最后将消息体转换为字节数组
            //发布消息到消息队列
            channel.basicPublish("", TASK_QUEUE_NAME,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
  }

}