package com.hbwl.service;

import com.hbwl.pojo.UserScore;

public interface UserScoreService {

    int addUserScore(String userId);

    UserScore getUserScore(String userId);

    int deleteUserScoreByUserId(String userId);

    int updateUserScoreByUserId(UserScore userScore);
}
