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
  `created_time` DATETIME NOT NULL COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL COMMENT '更新时间',
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