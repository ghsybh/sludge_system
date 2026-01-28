package com.sludge_system.dto;

import lombok.Data;

@Data
public class ImportFailure {
    private int row;
    private String reason;
}
