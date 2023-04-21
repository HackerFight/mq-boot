package com.qiuguan.mq.demo.listener;

import com.qiuguan.mq.demo.ann.AsyncTask;
import com.qiuguan.mq.demo.mapper.OrderDetailMapper;
import com.qiuguan.mq.demo.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;

/**
 * @author qiuguan
 */
@Slf4j
@AsyncTask(executorName = "mqTxExecutorService")
@RocketMQTransactionListener
public class CreateOrderCheckerListener implements RocketMQLocalTransactionListener {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        log.info("message: {}, args: {}", msg.getPayload(), arg);

        Object payload = msg.getPayload();

        try {
            //放到同一个本地事务中
//            this.orderMapper.saveOrder(order);
//            this.orderDetailMapper.saveOrderDetail(order.getOrderDetail());

            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            log.error("保存订单失败", e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        return null;
    }
}
