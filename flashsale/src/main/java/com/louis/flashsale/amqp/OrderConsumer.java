package com.louis.flashsale.amqp;

import com.louis.flashsale.config.RedisConfig;
import com.louis.flashsale.persistence.entity.SeckillItem;
import com.louis.flashsale.persistence.entity.SeckillOrder;
import com.louis.flashsale.persistence.entity.SeckillOrderPK;
import com.louis.flashsale.persistence.repository.SeckillItemRepository;
import com.louis.flashsale.persistence.repository.SeckillOrderRepository;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.louis.flashsale.config.DelayedRabbitConfig.DELAY_QUEUE_NAME;

@Component
public class OrderConsumer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillOrderRepository seckillOrderRepository;

    @Autowired
    private SeckillItemRepository seckillItemRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    @RabbitListener(queues = DELAY_QUEUE_NAME, ackMode = "MANUAL")
    public void process (SeckillOrderPK orderPK , Message message , Channel channel) throws IOException {
        SeckillOrder order = seckillOrderRepository.getById(orderPK);

        // 如果訂單未支付，則取消訂單，增加商品庫存
        if (order.getState() == 0) {

            logger.info("訂單[%d]支付逾時%n" , order.getItemId());
            logger.info("開始取消訂單......");

            // 將訂單設為無效訂單
            order.setState(-1);
            seckillOrderRepository.save(order);

            // 恢復庫存
            SeckillItem item = (SeckillItem)redisTemplate.opsForHash()
                                                         .get(RedisConfig.ITEM_KEY , orderPK.getItemId());
            item.setInventory(item.getInventory() + 1);

            redisTemplate.opsForHash()
                         .put(RedisConfig.ITEM_KEY , orderPK.getItemId() , item);
            seckillItemRepository.save(item);

            logger.info("訂單取消完畢");
        }
        try {
            channel.basicAck(message.getMessageProperties()
                                    .getDeliveryTag() , false);
        }
        catch (Exception e) {
            channel.basicReject(message.getMessageProperties()
                                       .getDeliveryTag() , false);
        }
    }
}