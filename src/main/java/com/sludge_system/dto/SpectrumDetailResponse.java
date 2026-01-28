package com.sludge_system.dto;

import com.sludge_system.domain.QcStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SpectrumDetailResponse {
    private Long id;
    private Long siteId;
    private String siteCode;
    private String siteName;
    private List<Double> bands;
    private Integer bandCount;
    private String source;
    private LocalDateTime capturedAt;
    private QcStatus qcStatus;
    private String qcMessage;
    private ChemLabelResponse label;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
