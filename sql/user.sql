CREATE DATABASE IF NOT EXISTS hbwl_user;
USE hbwl_user;

#注意，此数据库同时被gateway和service-user访问，前者模块只读，后者模块有读有写

#用户表，记录用户信息
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` VARCHAR(100) NOT NULL UNIQUE, #使用JavaUUID进行分配
  `username` VARCHAR(100) NOT NULL UNIQUE,
  `password` VARCHAR(100) NOT NULL,
  `role` VARCHAR(10) NOT NULL #'ADMIN', 'STUDENT', 'TEACHER'
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;