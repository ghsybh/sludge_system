package com.sludge_system.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportResponse {
    private int successCount;
    private int failCount;
    private List<ImportFailure> failures = new ArrayList<>();
}
