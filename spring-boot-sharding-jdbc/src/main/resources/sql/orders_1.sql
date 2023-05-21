SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
-- Table structure for orders_1

-- database lab18_orders_1
-- ----------------------------
DROP TABLE IF EXISTS `orders_1`;
CREATE TABLE `orders_1`(
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户编号',
    primary key (`id`)
) ENGINE=INNODB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单表';



-- ----------------------------
-- Table structure for orders_3
-- ----------------------------
DROP TABLE IF EXISTS `orders_3`;
CREATE TABLE `orders_3`(
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户编号',
    primary key (`id`)
) ENGINE=INNODB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单表';



-- ----------------------------
-- Table structure for orders_5
-- ----------------------------
DROP TABLE IF EXISTS `orders_5`;
CREATE TABLE `orders_5`(
                           `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
                           `user_id` BIGINT(20) NOT NULL COMMENT '用户编号',
                           primary key (`id`)
) ENGINE=INNODB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单表';


-- ----------------------------
-- Table structure for orders_7
-- ----------------------------
DROP TABLE IF EXISTS `orders_7`;
CREATE TABLE `orders_7`(
                           `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
                           `user_id` BIGINT(20) NOT NULL COMMENT '用户编号',
                           primary key (`id`)
) ENGINE=INNODB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单表';

SET FOREIGN_KEY_CHECKS = 1;