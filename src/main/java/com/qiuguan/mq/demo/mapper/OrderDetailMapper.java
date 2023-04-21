package com.qiuguan.mq.demo.mapper;

import com.qiuguan.mq.demo.biz.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author qiuguan
 */
@Mapper
public interface OrderDetailMapper {

    /**
     *  save order
     * @param orderDetail orderDetail info
     */
    void saveOrderDetail(OrderDetail orderDetail);
}
