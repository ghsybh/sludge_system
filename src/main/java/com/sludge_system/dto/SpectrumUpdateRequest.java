package com.sludge_system.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SpectrumUpdateRequest {
    private Long siteId;
    private List<Double> bands;
    private String source;
    private LocalDateTime capturedAt;
}
