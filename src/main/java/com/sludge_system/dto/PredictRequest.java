package com.sludge_system.dto;

import lombok.Data;

import java.util.List;

@Data
public class PredictRequest {
    private String taskType;
    private String model;
    private Long sampleId;
    private List<Double> bands;
}
