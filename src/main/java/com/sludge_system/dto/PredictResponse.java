package com.sludge_system.dto;

import lombok.Data;

import java.util.Map;

@Data
public class PredictResponse {
    private ClassificationResult classification;
    private RegressionResult regression;
    private Map<String, Object> metrics;

    @Data
    public static class ClassificationResult {
        private String siteCode;
        private Double confidence;
    }

    @Data
    public static class RegressionResult {
        private Double organic;
        private Double tn;
        private Double tp;
        private String unit;
    }
}
