package com.louis.flashsale.persistence.repository;

import com.louis.flashsale.persistence.entity.SeckillItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SeckillItemRepository extends JpaRepository<SeckillItem, Integer> {
}