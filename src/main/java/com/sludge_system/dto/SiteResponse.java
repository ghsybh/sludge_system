package com.sludge_system.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SiteResponse {
    private Long id;
    private String siteCode;
    private String siteName;
    private BigDecimal lat;
    private BigDecimal lng;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
