CREATE DATABASE IF NOT EXISTS hbwl_teaching;
USE hbwl_teaching;

#课程系列表，一个课程系列有多个课程
DROP TABLE IF EXISTS `course_section`;
CREATE TABLE `course_section`(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(20) NOT NULL COMMENT '课程系列名称',
  `description` VARCHAR(100) COMMENT '课程系列描述'
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#课程表
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(20) NOT NULL COMMENT '课程名称',
  `description` VARCHAR(100) COMMENT '课程描述',
  `section_id` INT NOT NULL COMMENT '课程系列id'
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#资料信息表，存储元数据，一份资料对应一节课，一节课只有一份资料
DROP TABLE IF EXISTS `material`;
CREATE TABLE `material` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `type` VARCHAR(20) NOT NULL COMMENT '资料类型',
  `description` VARCHAR(100) COMMENT '资料描述',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
  `course_id` INT NOT NULL COMMENT '课程id'
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#Markdown表
DROP TABLE IF EXISTS `md_content`;
CREATE TABLE `md_content` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `content` LONGTEXT NOT NULL COMMENT 'markdown内容',
  `mat_id` INT NOT NULL COMMENT '资料id'
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#本地文件表
DROP TABLE IF EXISTS `file_content`;
CREATE TABLE `file_content` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `type` VARCHAR(20) NOT NULL COMMENT '文件类型',
  `name` VARCHAR(100) NOT NULL COMMENT '文件名称',
  `size` INT NOT NULL COMMENT '文件大小',
  `mat_id` INT NOT NULL COMMENT '资料id'
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#用户调用记录表，用于记录用户调用agent的信息
DROP TABLE IF EXISTS `user_call_record`;
CREATE TABLE `user_call_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` VARCHAR(100) NOT NULL,
  `hour_timestamp` BIGINT NOT NULL COMMENT '小时时间戳',
  `call_count` INT NOT NULL COMMENT '调用次数',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#智能体对话记录表，用于记录agent对话记录
DROP TABLE IF EXISTS `agent_chat_memory`;
CREATE TABLE `agent_chat_memory` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `memory_id` VARCHAR(100) NOT NULL COMMENT '同userId',
  `content` JSON NOT NULL
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SELECT * FROM `agent_chat_memory`;

###以下为测试数据###
INSERT INTO `course_section`(`name`, `description`)
VALUES('测试课程1', '这是测试课程');
SELECT * FROM `course_section`;

INSERT INTO `course`(`name`, `description`, `section_id`)
VALUES('测试课1', '这是一节测试课', '1');
INSERT INTO `course`(`name`, `description`, `section_id`)
VALUES('测试课2', '这是一节测试课', '1');
SELECT * FROM `course`;

INSERT INTO `material`(`type`, `description`, `course_id`)
VALUES('pdf', '测试文档', '1');
SELECT * FROM `material`;

INSERT INTO `file_content`(`type`, `name`, `size`, `mat_id`)
VALUES('application/pdf', '192b0316-6716-46a3-a2b6-b65d89f6e15c.pdf', '1496687', '1');
SELECT * FROM `file_content`;