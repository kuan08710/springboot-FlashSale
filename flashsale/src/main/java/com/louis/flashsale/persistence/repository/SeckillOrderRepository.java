package com.louis.flashsale.persistence.repository;


import com.louis.flashsale.persistence.entity.SeckillOrder;
import com.louis.flashsale.persistence.entity.SeckillOrderPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeckillOrderRepository extends JpaRepository<SeckillOrder, SeckillOrderPK> {
}
