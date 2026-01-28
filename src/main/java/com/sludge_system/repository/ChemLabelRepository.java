package com.sludge_system.repository;

import com.sludge_system.domain.ChemLabel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChemLabelRepository extends JpaRepository<ChemLabel, Long> {
    Optional<ChemLabel> findBySampleId(Long sampleId);

    void deleteBySampleIdIn(List<Long> sampleIds);
}
