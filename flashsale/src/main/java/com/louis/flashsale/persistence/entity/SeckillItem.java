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
@Table(name = "seckill_item")
public class SeckillItem implements Serializable {

    private static final long serialVersionUID = 3074398177059694330L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                // 商品ID
    private String title;              // 商品名稱
    private Float price;               // 商品原價
    private Float seckillPrice;        // 限時搶購價格
    private Integer inventory;         // 商品庫存

    @Column(name = "img")
    private String imgUrl;             // 商品圖片URL

    // @JsonDeserialize 和 @JsonSerialize 用於解決 Redis序列化 Java8日期類型的例外
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startTime;   // 限時搶購開始時間

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endTime;     // 限時搶購結束時間

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;  // 商品建立時間
}
