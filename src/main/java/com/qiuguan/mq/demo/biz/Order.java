package com.qiuguan.mq.demo.biz;

import lombok.Data;

/**
 * @author qiuguan
 */
@Data
public class Order {

    private String orderId;

    private String buyer;

    private OrderDetail orderDetail;
}
