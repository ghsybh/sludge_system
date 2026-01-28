package com.sludge_system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ValidateRequest {
    @NotNull(message = "bands is required")
    private List<Double> bands;
}
