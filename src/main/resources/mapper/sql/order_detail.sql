CREATE TABLE `order_detail` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `phone` varchar(11) DEFAULT NULL,
    `address` varchar(255) DEFAULT NULL,
    `order_detail_id` varchar(64) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci