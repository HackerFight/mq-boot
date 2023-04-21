package com.qiuguan.mq.demo.controller;

import com.qiuguan.mq.demo.biz.Order;
import com.qiuguan.mq.demo.biz.OrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * @author qiuguan
 */
@Slf4j
@Controller
public class OrderCreateController {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    @GetMapping("/order/{buyer}")
    public void createOrder(@PathVariable String buyer) {
        Order order = new Order();
        String orderId = UUID.randomUUID().toString();
        order.setOrderId(orderId);
        order.setBuyer(buyer);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setPhone("18883858508");
        orderDetail.setAddress("上海外滩");
        orderDetail.setOrderDetailId(UUID.randomUUID().toString());

        order.setOrderDetail(orderDetail);

        Message<Order> message = MessageBuilder.withPayload(order).build();

        try {
            TransactionSendResult result = rocketMQTemplate.sendMessageInTransaction("tx_topic", message, orderId);
            log.info("发送消息成功, result: {}", result);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
