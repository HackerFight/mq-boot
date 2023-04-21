package com.qiuguan.mq.demo.mapper;

import com.qiuguan.mq.demo.biz.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author qiuguan
 */
@Mapper
public interface OrderMapper {

    void saveOrder(Order order);

    Order getOrder(String orderId);
}
