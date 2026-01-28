package com.sludge_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SiteCreateRequest {
    @NotBlank(message = "siteCode is required")
    private String siteCode;
    private String siteName;
    private BigDecimal lat;
    private BigDecimal lng;
    private String remark;
}
