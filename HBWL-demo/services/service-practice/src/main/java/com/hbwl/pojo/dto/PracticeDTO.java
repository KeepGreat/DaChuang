package com.hbwl.pojo.dto;

import com.hbwl.pojo.Practice;
import com.hbwl.pojo.PracticeIndex;
import lombok.Data;

@Data
public class PracticeDTO {
    private Practice practice;
    private PracticeIndex practiceIndex;
}
