package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.PracticeIndex;

import java.util.List;

public interface PracticeIndexService {

    int addPracticeIndexes(List<PracticeIndex> indexList);

    int deletePracticeIndexesByIds(List<Integer> indexIds);

    int updatePracticeIndexById(PracticeIndex practiceIndex);

    List<PracticeIndex> getPracticeIndexes(PracticeIndex practiceIndex);

    Page<PracticeIndex> getPracticeIndexesPage(int pageNo, int pageSize, PracticeIndex practiceIndex);
}
