package com.hbwl.pojo;

import lombok.Data;

@Data
public class UserScore {
    private Integer id;
    private String userId;
    private Float totalScore;
    private String eachScore;
}
