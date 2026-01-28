package com.sludge_system.repository;

import com.sludge_system.domain.QcStatus;
import com.sludge_system.domain.SpectrumSample;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpectrumSampleRepository extends JpaRepository<SpectrumSample, Long> {
    Page<SpectrumSample> findBySiteId(Long siteId, Pageable pageable);

    Page<SpectrumSample> findByQcStatus(QcStatus qcStatus, Pageable pageable);

    Page<SpectrumSample> findBySiteIdAndQcStatus(Long siteId, QcStatus qcStatus, Pageable pageable);

    boolean existsBySiteId(Long siteId);

    @Query("select s.id from SpectrumSample s where s.site.id = :siteId")
    List<Long> findIdsBySiteId(Long siteId);

    void deleteByIdIn(List<Long> ids);
}
