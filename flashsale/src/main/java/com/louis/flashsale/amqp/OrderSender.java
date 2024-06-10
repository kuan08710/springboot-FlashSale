package com.louis.flashsale.amqp;

import com.louis.flashsale.persistence.entity.SeckillOrderPK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.louis.flashsale.config.DelayedRabbitConfig.DELAY_EXCHANGE_NAME;
import static com.louis.flashsale.config.DelayedRabbitConfig.DELAY_QUEUE_NAME;

@Component
public class OrderSender {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Integer DELAY_TIME = 10 * 60 * 1000; // 延遲 10 分鐘
    @Autowired
    private AmqpTemplate rabbitTemplate;

    /**
     * 發送延遲訊息，客戶過期未支付，取消訂單
     *
     * @param orderPK 限時搶購訂單主鍵
     */
    public void sendDelayMsg (SeckillOrderPK orderPK) {
        rabbitTemplate.convertAndSend(DELAY_EXCHANGE_NAME , DELAY_QUEUE_NAME , orderPK , message -> {

            message.getMessageProperties()
                   .setDeliveryMode(MessageDeliveryMode.PERSISTENT);

            //指定訊息延遲的時長為10分鐘，以毫秒為單位
            message.getMessageProperties()
                   .setDelay(DELAY_TIME);
            return message;
        });

        logger.info(" 當前的時間是：" + new Date());
        logger.info(" [x] Sent '" + orderPK + "'");
    }
}