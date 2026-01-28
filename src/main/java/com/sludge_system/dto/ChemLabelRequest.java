package com.sludge_system.dto;

import lombok.Data;

@Data
public class ChemLabelRequest {
    private Double organic;
    private Double tn;
    private Double tp;
    private String unit;
}
