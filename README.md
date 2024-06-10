  # flaseSale

## 項目簡介

商品限時搶購系統，使用 Redis 來快取限時搶購的商品，業務層面上，採用 Spring 的事務管理來避免出現資料不一致的情況。
當使用者限時搶購了商品後，需要在規定時間內支付，若未進行支付就需要取消逾時訂單，這個功能採用 RabbitMQ 的延遲訊息佇列實作。
資料存取使用 JPA，網頁顯示用 Thymeleaf 模板引擎，用自行定義的異常類，針對不同的情況拋出和回傳訊息。

### 使用技術和版本

- **Spring Boot**: 2.6.4
- **MySQL**: 8.3.0
- **JPA**: 數據庫訪問
- **Redis**: 快取
- **RabbitMQ**: 訊息佇列
- **Thymeleaf**: 模板引擎
- **devtools**: 熱部署
- **Lombok**: 簡化程式碼
- **Maven**: 項目構建和依賴管理

## 項目結構

```plaintext
├── src/main/java
│   └── com/louis/flashsale
│       ├── amqp
│       │   ├── OrderConsumer.java
│       │   └── OrderSender.java
│       ├── config
│       │   ├── DelayedRabbitConfig.java
│       │   └── RedisConfig.java
│       ├── controller
│       │   └── SeckillController.java
│       ├── exception
│       │   ├── InsufficientInventoryException.java
│       │   ├── OrderInvalidationException.java
│       │   ├── RepeatSeckillException.java
│       │   ├── SeckillException.java
│       │   └── UnpaidException.java
│       ├── persistence
│       │   ├── entity
│       │   │   ├── SeckillItem.java
│       │   │   ├── SeckillOrder.java
│       │   │   ├── SeckillOrderPK.java
│       │   └── repository
│       │       ├── SeckillItemRepository.java
│       │       └── SeckillOrderRepository.java
│       ├── service
│       │   └── SeckillSevice.java
│       └── FlashsaleApplication.java
├── src/main/resources
│   └── application.properties
├── src/test/java
│   └── com/louis/flashsale
│       └── FlashsaleApplicationTestsFlashsaleApplicationTests.java
├── .gitignore
├── pom.xml
└── README.md
