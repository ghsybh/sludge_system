package com.sludge_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SpectrumCreateRequest {
    private Long siteId;
    @NotNull(message = "bands is required")
    private List<Double> bands;
    @NotBlank(message = "source is required")
    private String source;
    private LocalDateTime capturedAt;
    private ChemLabelRequest label;
}
