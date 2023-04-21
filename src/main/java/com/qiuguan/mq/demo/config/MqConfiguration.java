package com.qiuguan.mq.demo.config;

import com.qiuguan.mq.demo.ann.AsyncTask;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.autoconfigure.RocketMQTransactionConfiguration;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author qiuguan
 */
@Configuration
public class MqConfiguration {

    @Bean(name = "mqTxExecutorService")
    public ExecutorService executorService(){
        return new ThreadPoolExecutor(5, 15, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
               Thread t = new Thread(r);
               t.setName("qiuguan-mq-async-task-executor-");
               return t;
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());
    }


    /**
     * 重写 RocketMq 自带的 RocketMQTransactionConfiguration 配置类，因为他无法灵活的指定线程池
     */
    @SuppressWarnings("all")
    @Configuration
    public static class RocketMQTransactionConfigurationAdapter extends RocketMQTransactionConfiguration {

        private ConfigurableApplicationContext applicationContext;

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = (ConfigurableApplicationContext)applicationContext;
        }

        @Override
        public void afterSingletonsInstantiated() {
            Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(RocketMQTransactionListener.class)
                    .entrySet().stream().filter(entry -> !ScopedProxyUtils.isScopedTarget(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            beans.forEach(this::registerTransactionListener);
        }

        protected void registerTransactionListener(String beanName, Object bean) {
            Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);

            if (!RocketMQLocalTransactionListener.class.isAssignableFrom(bean.getClass())) {
                throw new IllegalStateException(clazz + " is not instance of " + RocketMQLocalTransactionListener.class.getName());
            }
            RocketMQTransactionListener annotation = clazz.getAnnotation(RocketMQTransactionListener.class);
            RocketMQTemplate rocketMQTemplate = (RocketMQTemplate) applicationContext.getBean(annotation.rocketMQTemplateBeanName());
            if (((TransactionMQProducer) rocketMQTemplate.getProducer()).getTransactionListener() != null) {
                throw new IllegalStateException(annotation.rocketMQTemplateBeanName() + " already exists RocketMQLocalTransactionListener");
            }

            AsyncTask asyncTask = clazz.getAnnotation(AsyncTask.class);
            if (asyncTask == null) {
                ((TransactionMQProducer) rocketMQTemplate.getProducer()).setExecutorService(new ThreadPoolExecutor(annotation.corePoolSize(), annotation.maximumPoolSize(),
                        annotation.keepAliveTime(), annotation.keepAliveTimeUnit(), new LinkedBlockingDeque<>(annotation.blockingQueueSize())));
                ((TransactionMQProducer) rocketMQTemplate.getProducer()).setTransactionListener(RocketMQUtil.convert((RocketMQLocalTransactionListener) bean));
                return;
            }

            String executorName = asyncTask.executorName();
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)this.applicationContext.getBean(executorName);
            if (threadPoolExecutor == null) {
                throw new IllegalStateException("....... annotation @AsyncTask Does not specify a thread pool name ........");
            }

            ((TransactionMQProducer) rocketMQTemplate.getProducer()).setExecutorService(threadPoolExecutor);
        }
    }
}
