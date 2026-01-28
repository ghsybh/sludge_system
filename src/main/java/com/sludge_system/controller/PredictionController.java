package com.sludge_system.controller;

import com.sludge_system.common.ApiResponse;
import com.sludge_system.dto.PredictRequest;
import com.sludge_system.dto.PredictResponse;
import com.sludge_system.dto.PredictionHistoryResponse;
import com.sludge_system.service.PredictionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PredictionController {
    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/predict")
    public ApiResponse<PredictResponse> predict(@Valid @RequestBody PredictRequest request) {
        return ApiResponse.ok(predictionService.predict(request));
    }

    @GetMapping("/predictions")
    public ApiResponse<Page<PredictionHistoryResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "20") int size,
                                                             @RequestParam(required = false) String taskType) {
        return ApiResponse.ok(predictionService.list(taskType, PageRequest.of(page, size)));
    }

    @GetMapping("/predictions/{id}")
    public ApiResponse<PredictionHistoryResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(predictionService.get(id));
    }

    @DeleteMapping("/predictions/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        predictionService.delete(id);
        return ApiResponse.ok(null);
    }
}
