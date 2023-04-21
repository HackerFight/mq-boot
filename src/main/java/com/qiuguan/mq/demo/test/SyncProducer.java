package com.qiuguan.mq.demo.test;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * @author qiuguan
 * @version SyncProducer.java, v 0.1 2022/03/25  18:03:24 qiuguan Exp $
 * 同步发送消息
 *
 * <p>
 *     public enum SendStatus {
 *         SEND_OK,
 *
 *         // 刷盘超时。当Broker设置的刷盘策略为同步刷盘时才可能出 现这种异常状态。异步刷盘不会出现
 *         FLUSH_DISK_TIMEOUT,
 *
 *         // Slave同步超时。当Broker集群设置的Master-Slave的复 制方式为同步复制时才可能出现这种异常状态。异步复制不会出现
 *         FLUSH_SLAVE_TIMEOUT,
 *
 *         // 没有可用的Slave。当Broker集群设置为Master-Slave的 复制方式为同步复制时才可能出现这种异常状态。异步复制不会出现
 *         SLAVE_NOT_AVAILABLE,
 *    }
 *
 * </p>
 */
public class SyncProducer {


    public static void main(String[] args) throws Exception {

        // 实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer("qiuguan-p-group");
        // 设置NameServer的地址
        producer.setNamesrvAddr("47.96.111.179:9876");
        //producer.setSendLatencyFaultEnable(true);
        producer.setDefaultTopicQueueNums(1);

        // 启动Producer实例
        producer.start();

        for (int i = 1; i <= 1; i++) {
            // 创建消息，并指定Topic，Tag和消息体
            Message msg = new Message("test-topic",
                    "*",
                    ("Hello RocketMQ-" + i).getBytes(RemotingHelper.DEFAULT_CHARSET)
            );
            //指定消息key, 消费者可以根据key做幂等消费
            msg.setKeys("key-unique-" + i);

            //同步发送
            SendResult sendResult = producer.send(msg);

            // 通过sendResult返回消息是否成功送达
            System.out.printf("%s%s%n", sendResult, i);
        }


        // 如果不再发送消息，关闭Producer实例。
        producer.shutdown();
    }
}