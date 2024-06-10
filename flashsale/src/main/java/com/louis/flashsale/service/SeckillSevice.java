package com.louis.flashsale.service;

import com.louis.flashsale.amqp.OrderSender;
import com.louis.flashsale.config.RedisConfig;
import com.louis.flashsale.exception.*;
import com.louis.flashsale.persistence.entity.SeckillItem;
import com.louis.flashsale.persistence.entity.SeckillOrder;
import com.louis.flashsale.persistence.entity.SeckillOrderPK;
import com.louis.flashsale.persistence.repository.SeckillItemRepository;
import com.louis.flashsale.persistence.repository.SeckillOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SeckillSevice {

    @Autowired
    private SeckillItemRepository seckillItemRepository;

    @Autowired
    private SeckillOrderRepository seckillOrderRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderSender orderSender;

    /**
     * 獲取所有限時搶購商品，
     * 如果商品資訊在 Redis 中不存在，則在第一次從資料庫中獲取商品資訊時，將該資訊儲存到 Redis 中
     *
     * @return List<SeckillItem> 限時搶購商品列表
     */
    public List<SeckillItem> getAllItem () {
        List<SeckillItem> items = redisTemplate.opsForHash()
                                               .values(RedisConfig.ITEM_KEY);

        if (items == null || items.size() == 0) {
            items = seckillItemRepository.findAll();
            for (SeckillItem item : items) {
                redisTemplate.opsForHash()
                             .put(RedisConfig.ITEM_KEY , item.getId() , item);
            }
        }

        return items;
    }

    /**
     * 獲取限時搶購商品資訊。判斷使用者訂單是否已經存在，
     * 如果已經存在，則判斷訂單狀態 ;
     * 如果不存在，則傳回使用者選擇的限時搶購商品
     * 將該資訊儲存到 Redis 中
     *
     * @param id     商品ID
     * @param mobile 用戶手機
     * @return SeckillItem 商品物件
     * @throws SeckillException
     */
    public SeckillItem getItemById (Integer id , String mobile) throws SeckillException {
        SeckillOrderPK seckillOrderPK = new SeckillOrderPK(id , mobile);
        Optional<SeckillOrder> optionalOrder = seckillOrderRepository.findById(seckillOrderPK);
        if (!optionalOrder.isEmpty()) {
            SeckillOrder order = optionalOrder.get();

            // 訂單已支付，重複秒殺
            if (order.getState() == 1) {
                throw new RepeatSeckillException();
            }
            // 已限時搶購，但還未支付
            else if (order.getState() == 0) {
                throw new UnpaidException();
            }
            // 訂單已經故障
            else {
                throw new OrderInvalidationException();
            }
        }

        SeckillItem item = (SeckillItem)redisTemplate.opsForHash()
                                                     .get(RedisConfig.ITEM_KEY , id);
        if (item == null) {
            item = seckillItemRepository.getById(id);
        }

        return item;
    }

    /**
     * 執行限時搶購邏輯
     * 先判斷庫存是否充足，如果庫存充足，則存儲使用者訂單，並遞減庫存，
     * 同時發送延遲訂單訊息，以便在使用者未在規定時間支付時取消訂單
     *
     * @param id     商品ID
     * @param mobile 用戶手機
     * @throws InsufficientInventoryException
     */
    public void execSeckill (Integer id , String mobile) throws InsufficientInventoryException {
        SeckillItem item = (SeckillItem)redisTemplate.opsForHash()
                                                     .get(RedisConfig.ITEM_KEY , id);
        Integer inventory = item.getInventory();

        // 如果庫存不夠，則拋出例外，交給Web層進行處理
        if (inventory <= 0) {
            throw new InsufficientInventoryException();
        }
        // 將庫存遞減 1
        item.setInventory(item.getInventory() - 1);
        redisTemplate.opsForHash()
                     .put(RedisConfig.ITEM_KEY , id , item);
        seckillItemRepository.save(item);

        //儲存訂單
        SeckillOrder order = new SeckillOrder();
        order.setItemId(id);
        order.setMobile(mobile);
        order.setMoney(item.getSeckillPrice());
        order.setState(0);
        seckillOrderRepository.save(order);

        // 發送延遲訂單處理訊息
        orderSender.sendDelayMsg(new SeckillOrderPK(id , mobile));

    }

    /**
     * 訂單支付。將訂單狀態設定為已支付
     *
     * @param id     商品ID
     * @param mobile 用戶手機號
     */
    public void pay (Integer id , String mobile) {
        SeckillOrder order = seckillOrderRepository.getById(new SeckillOrderPK(id , mobile));
        int state = order.getState();

        // 如果是未支付，則設定為已支付
        if (state == 0) {
            order.setState(1);
            seckillOrderRepository.save(order);
        }
        // 如果訂單已經故障，則拋出例外
        else if (state == -1) {
            throw new OrderInvalidationException();
        }
    }
}