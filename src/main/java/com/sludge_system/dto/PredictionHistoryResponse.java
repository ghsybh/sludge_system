package com.sludge_system.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class PredictionHistoryResponse {
    private Long id;
    private String taskType;
    private String modelType;
    private Long inputSampleId;
    private List<Double> inputBands;
    private Long predSiteId;
    private String predSiteCode;
    private Double predOrganic;
    private Double predTn;
    private Double predTp;
    private Map<String, Object> metrics;
    private LocalDateTime createdAt;
}
