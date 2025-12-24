package com.hbwl.pojo.dto;

import com.hbwl.pojo.Question;
import com.hbwl.pojo.QuestionIndex;
import lombok.Data;

@Data
public class QuestionDTO {
    private Question question;
    private QuestionIndex questionIndex;
}
