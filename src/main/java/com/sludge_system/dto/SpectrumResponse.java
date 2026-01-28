package com.sludge_system.dto;

import com.sludge_system.domain.QcStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SpectrumResponse {
    private Long id;
    private Long siteId;
    private String siteCode;
    private Integer bandCount;
    private String source;
    private LocalDateTime capturedAt;
    private QcStatus qcStatus;
    private String qcMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
