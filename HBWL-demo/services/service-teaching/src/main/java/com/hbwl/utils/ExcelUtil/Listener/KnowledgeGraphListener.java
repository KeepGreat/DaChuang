package com.hbwl.utils.ExcelUtil.Listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.hbwl.pojo.dto.KnowledgeGraphDTO;
import com.hbwl.utils.ExcelUtil.Store.KnowledgeGraphStore;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeGraphListener implements ReadListener<KnowledgeGraphDTO> {

    private final int cacheSize = 100;
    private final List<KnowledgeGraphDTO> cache = new ArrayList<>(cacheSize);
    private final KnowledgeGraphStore knowledgeGraphStore;
    private final Integer courseSectionId;

    public KnowledgeGraphListener(KnowledgeGraphStore knowledgeGraphStore, Integer courseSectionId) {
        this.knowledgeGraphStore = knowledgeGraphStore;
        this.courseSectionId = courseSectionId;
    }

    @Override
    public void invoke(KnowledgeGraphDTO knowledgeGraphDTO, AnalysisContext analysisContext) {
        if (knowledgeGraphDTO == null) {
            return;
        }

        // 读到下一个一级节点前先落库，确保“一级节点子树”整体写入
        if (isLevel1Row(knowledgeGraphDTO) && !cache.isEmpty()) {
            flushCache();
        }

        cache.add(knowledgeGraphDTO);
        if (cache.size() >= cacheSize) {
            // 兜底分批，避免某个一级节点下数据过大导致内存占用过高
            flushCache();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        flushCache();
        knowledgeGraphStore.clearImportContext(courseSectionId);
    }

    private boolean isLevel1Row(KnowledgeGraphDTO dto) {
        String level1 = dto.getLevel1NodeName();
        return level1 != null && !level1.trim().isEmpty();
    }

    private void flushCache() {
        if (cache.isEmpty()) {
            return;
        }
        knowledgeGraphStore.storeKnowledgeGraph(cache, courseSectionId);
        cache.clear();
    }
}
