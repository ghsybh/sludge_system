package com.sludge_system.validation;

import com.sludge_system.domain.QcStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QcResult {
    private boolean valid;
    private QcStatus qcStatus;
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private String qcMessage;
}
