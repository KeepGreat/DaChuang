CREATE DATABASE IF NOT EXISTS hbwl_practice;
USE hbwl_practice;

#问题课程索引表，通过课程id可以查询相关问题的id数组，还可以定义问题类型便于后续扩展
DROP TABLE IF EXISTS `question_index`;
CREATE TABLE `question_index` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `question_id` INT NOT NULL COMMENT '问题id',
  `course_section_id` INT NOT NULL COMMENT '课程系列id', #课程系列对应id
  `course_id` INT NOT NULL DEFAULT '0' COMMENT '课程id' #课程对应id，若无则为0
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#问题表，问题分为两类：常规题与编程题，编程题需要有测试用例和答案
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(20) COMMENT '问题名称',
  `type` TINYINT(1) NOT NULL COMMENT '问题类型', #0：选择，1：简答，2：编程
  `location` TINYINT(1) NOT NULL COMMENT '问题定位类型', #课程问题为0，课程内章节为1
  `content` TEXT NOT NULL COMMENT '问题文本', #<65535字
  `has_resource` BOOLEAN NOT NULL COMMENT '是否有资源' #优化查询
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#问题资源文件表，与问题相关的资源
#文件存储在文件系统，该表只是记录文件信息的表
DROP TABLE IF EXISTS `question_resource`;
CREATE TABLE `question_resource` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `description` VARCHAR(20) COMMENT '资源描述',
  `name` VARCHAR(100) NOT NULL COMMENT '资源名称', 
  `type` TINYINT(1) NOT NULL COMMENT '资源类型', #0：测试用例，1：用例答案，2：问题描述资料
  `size` INT NOT NULL COMMENT '资源大小',
  `question_id` INT NOT NULL COMMENT '问题id'
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#问题答案表，记录问题的答案
DROP TABLE IF EXISTS `answer`;
CREATE TABLE `answer` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `content` TEXT NOT NULL COMMENT '问题答案',
  `analysis` TEXT COMMENT '答案解析',
  `question_id` INT NOT NULL COMMENT '问题id'
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;