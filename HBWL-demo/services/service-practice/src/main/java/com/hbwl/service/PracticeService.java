package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.Practice;
import com.hbwl.pojo.PracticeIndex;
import com.hbwl.pojo.PracticeType;

import java.time.LocalDateTime;
import java.util.List;

public interface PracticeService {

    int addPractice(Practice practice, PracticeIndex practiceIndex);

    int deletePractice(Integer id);

    int deletePractices(PracticeIndex practiceIndex);

    int updatePracticeById(Practice practice);

    List<Practice> getPractices(Practice practice, LocalDateTime createdAtStart, LocalDateTime createdAtEnd, LocalDateTime expiredAtStart, LocalDateTime expiredAtEnd);

    Page<Practice> getPracticesPage(int pageNo, int pageSize,
                                    Practice practice, LocalDateTime createdAtStart, LocalDateTime createdAtEnd, LocalDateTime expiredAtStart, LocalDateTime expiredAtEnd);

    List<Practice> getPracticesByIndex(PracticeIndex practiceIndex);

    Page<Practice> getPracticesPageByIndex(int pageNo, int pageSize, PracticeIndex practiceIndex);

    //管理PracticeType

    int addPracticeType(PracticeType practiceType);

    int deletePracticeTypeById(Integer id);

    int updatePracticeTypeById(PracticeType practiceType);

    List<PracticeType> getPracticeTypes(PracticeType practiceType);
}
