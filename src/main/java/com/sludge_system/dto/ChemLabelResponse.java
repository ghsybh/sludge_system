package com.sludge_system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChemLabelResponse {
    private Long id;
    private Long sampleId;
    private Double organic;
    private Double tn;
    private Double tp;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
