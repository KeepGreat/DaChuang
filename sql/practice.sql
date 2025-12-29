CREATE DATABASE IF NOT EXISTS hbwl_practice;
USE hbwl_practice;

#查询问题的方法：
#1.查询单节课course的课后问题：在练习课程索引表中根据course_section_id和course_id进行查询获得练习id，再从练习问题索引表中进行查询获取问题组
#2.查询整个课程courseSection的练习问题：在练习课程索引表中根据course_section_id进行查询获得练习id组，再从练习问题索引表中进行查询获取问题组

#练习课程索引表，通过课程id可以查询相关练习的id数组
#单节课练习：两个id都有；整个课程的练习：只有course_section_id，course_id为0
DROP TABLE IF EXISTS `practice_index`;
CREATE TABLE `practice_index` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `practice_id` INT NOT NULL COMMENT '问题id',
  `course_section_id` INT NOT NULL COMMENT '课程系列id', #课程系列对应id
  `course_id` INT NOT NULL DEFAULT '0' COMMENT '课程id' #课程对应id，若无则为0
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#练习表，一组问题组成一个练习
DROP TABLE IF EXISTS `practice`;
CREATE TABLE `practice` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(20) NOT NULL COMMENT '练习名称',
  `question_num` SMALLINT NOT NULL COMMENT '问题数量',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '发布时间',
  `expired_at` DATETIME COMMENT '截止时间' #为空表示长期有效
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#练习问题索引表，根据练习id查询相关问题的id数组
DROP TABLE IF EXISTS `question_index`;
CREATE TABLE `question_index` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `practice_id` INT NOT NULL COMMENT '练习id',
  `question_id` INT NOT NULL COMMENT '问题id'
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#问题表，问题分为两类：常规题与编程题，编程题需要有测试用例和答案
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(20) COMMENT '问题名称',
  `type` TINYINT(1) NOT NULL COMMENT '问题类型', #0：判断，1：选择，2：简答，3：编程
  `content` TEXT NOT NULL COMMENT '问题文本', #<65535字
  `score` TINYINT NOT NULL COMMENT '问题分值',
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

#用户答案表，记录用户的答案
DROP TABLE IF EXISTS `user_answer`;
CREATE TABLE `user_answer`(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `content` TEXT NOT NULL COMMENT '用户答案',
  `user_id` VARCHAR(100) NOT NULL COMMENT '用户id',
  `question_id` INT NOT NULL COMMENT '问题id',
  `question_type` TINYINT(1) NOT NULL COMMENT '问题类型', #0：判断，1：选择，2：简答，3：编程, 冗余该字段为了便于查找
  `score` TINYINT NOT NULL DEFAULT '-1' COMMENT '得分', #得分应小于等于问题分值,-1代表未批改
  `comment` VARCHAR(500) COMMENT '答案评价' #记录教师或ai对答案的评价 
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

###以下是测试用例###
INSERT INTO `practice`(`name`, `question_num`)
VALUES('测试练习', '4');
SELECT * FROM `practice`;

INSERT INTO `practice_index`(`practice_id`, `course_section_id`)
VALUES('1', '1');
SELECT * FROM `practice_index`;

INSERT INTO `question`(`name`, `type`, `content`, `score`, `has_resource`)
VALUES('测试判断题', '0', '这是测试判断题，正确答案是False', '100', FALSE);
INSERT INTO `question`(`name`, `type`, `content`, `score`, `has_resource`)
VALUES('测试选择题', '1', '这是测试选择题，正确答案是C', '100', FALSE);
INSERT INTO `question`(`name`, `type`, `content`, `score`, `has_resource`)
VALUES('测试简答题', '2', '这是测试简答题', '100', FALSE);
INSERT INTO `question`(`name`, `type`, `content`, `score`, `has_resource`)
VALUES('斐波那契数列', '3', '计算斐波那契数列的第n项,斐波那契数列的定义为F(1)=1, F(2)=1, F(n)=F(n-1)+F(n-2),(n≥3)', '100', TRUE);
SELECT * FROM `question`;

INSERT INTO `question_index`(`practice_id`, `question_id`)
VALUES('1', '1');
INSERT INTO `question_index`(`practice_id`, `question_id`)
VALUES('1', '2');
INSERT INTO `question_index`(`practice_id`, `question_id`)
VALUES('1', '3');
INSERT INTO `question_index`(`practice_id`, `question_id`)
VALUES('1', '4');
SELECT * FROM `question_index`;

INSERT INTO `question_resource`(`description`, `name`, `type`, `size`, `question_id`)
VALUES('斐波那契数列测试用例', '1.in', '0', '4', '1');
INSERT INTO `question_resource`(`description`, `name`, `type`, `size`, `question_id`)
VALUES('斐波那契数列用例答案', '1.out', '1', '2', '1');
SELECT * FROM `question_resource`;

INSERT INTO `answer`(`content`, `analysis`, `question_id`)
VALUES('False', '这是测试解析', 2);
INSERT INTO `answer`(`content`, `analysis`, `question_id`)
VALUES('C', '这是测试解析', 3);
INSERT INTO `answer`(`content`, `analysis`, `question_id`)
VALUES('这是测试答案', '这是测试解析', 4);
SELECT * FROM `answer`;

SELECT * FROM `user_answer`;