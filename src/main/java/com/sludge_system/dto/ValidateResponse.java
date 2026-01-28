package com.sludge_system.dto;

import com.sludge_system.domain.QcStatus;
import lombok.Data;

import java.util.List;

@Data
public class ValidateResponse {
    private boolean isValid;
    private QcStatus qcStatus;
    private List<String> errors;
    private List<String> warnings;
}
