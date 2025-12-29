CREATE DATABASE IF NOT EXISTS hbwl_evaluation;
USE hbwl_evaluation;

DROP TABLE IF EXISTS `user_score`;
CREATE TABLE `user_score`(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` VARCHAR(100) NOT NULL COMMENT '用户id',
  `total_score` FLOAT(4,1) NOT NULL COMMENT '总体分数',
  `each_score` JSON NOT NULL COMMENT '具体分数' #JSON字符串
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SELECT * FROM `user_score`;