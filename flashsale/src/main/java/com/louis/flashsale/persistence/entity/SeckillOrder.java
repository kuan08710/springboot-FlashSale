package com.louis.flashsale.persistence.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
@NoArgsConstructor
@Entity
@IdClass(SeckillOrderPK.class)
@Table(name = "seckill_order")
public class SeckillOrder implements Serializable {

    private static final long serialVersionUID = 1580657924475702411L;

    @Id
    @Column(name = "item_id")
    private Integer itemId;            // 商品ID
    @Id
    @Column(name = "mobile")
    private String mobile;             // 手機號

    private Float money;               // 支付金額

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;   // 訂單建立時間

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime paymentTime;  // 訂單支付時間

    private Integer state;              // 訂單狀態
}
