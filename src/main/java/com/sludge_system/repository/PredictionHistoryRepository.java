package com.sludge_system.repository;

import com.sludge_system.domain.PredictionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredictionHistoryRepository extends JpaRepository<PredictionHistory, Long> {
    Page<PredictionHistory> findByTaskType(String taskType, Pageable pageable);

    void deleteByInputSampleIdIn(List<Long> sampleIds);

    void deleteByPredSiteId(Long siteId);
}
