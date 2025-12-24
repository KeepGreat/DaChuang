package com.hbwl.pojo;

import lombok.Data;

//代码评估相关的评分
@Data
public class TestScore {
    private int readability;  //代码可读性评分
    private int structure;   //代码结构性评分
    private int modularity;   //代码模块性评分
    private int guidelines;   //代码规范性评分
}
