package com.sludge_system.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SiteUpdateRequest {
    private String siteCode;
    private String siteName;
    private BigDecimal lat;
    private BigDecimal lng;
    private String remark;
}
