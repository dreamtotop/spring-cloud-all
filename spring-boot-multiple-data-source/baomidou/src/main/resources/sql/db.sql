SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
-- Table structure for users

-- database test_users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`(
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户id',
    `user_name` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password`  VARCHAR(64) NOT NULL COMMENT '用户密码',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    primary key (`id`),
    UNIQUE key `idx_user_name`(`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for orders

-- database test_orders
-- ----------------------------

CREATE TABLE `orders` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
    `user_id` BIGINT(20) DEFAULT NULL COMMENT '用户编号',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单表';



SET FOREIGN_KEY_CHECKS = 1;

