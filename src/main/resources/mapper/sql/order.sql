CREATE TABLE `order` (
     `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
     `order_id` varchar(64) NOT NULL,
     `order_detail_id` varchar(64) DEFAULT NULL,
     `buyer` varchar(32) DEFAULT NULL,
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci