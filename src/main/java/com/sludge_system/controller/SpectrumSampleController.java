package com.sludge_system.controller;

import com.sludge_system.common.ApiResponse;
import com.sludge_system.domain.QcStatus;
import com.sludge_system.dto.*;
import com.sludge_system.service.SpectrumImportService;
import com.sludge_system.service.SpectrumSampleService;
import com.sludge_system.validation.QcResult;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/spectra")
public class SpectrumSampleController {
    private final SpectrumSampleService spectrumService;
    private final SpectrumImportService importService;

    public SpectrumSampleController(SpectrumSampleService spectrumService,
                                    SpectrumImportService importService) {
        this.spectrumService = spectrumService;
        this.importService = importService;
    }

    @PostMapping
    public ApiResponse<SpectrumResponse> create(@Valid @RequestBody SpectrumCreateRequest request) {
        return ApiResponse.ok(spectrumService.create(request));
    }

    @GetMapping
    public ApiResponse<Page<SpectrumResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size,
                                                    @RequestParam(required = false) Long siteId,
                                                    @RequestParam(required = false) QcStatus qcStatus) {
        return ApiResponse.ok(spectrumService.list(siteId, qcStatus, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<SpectrumDetailResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(spectrumService.getDetail(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<SpectrumResponse> update(@PathVariable Long id,
                                                @RequestBody SpectrumUpdateRequest request) {
        return ApiResponse.ok(spectrumService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        spectrumService.delete(id);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{id}/label")
    public ApiResponse<ChemLabelResponse> upsertLabel(@PathVariable Long id,
                                                      @RequestBody ChemLabelRequest request) {
        return ApiResponse.ok(spectrumService.upsertLabel(id, request));
    }

    @GetMapping("/{id}/label")
    public ApiResponse<ChemLabelResponse> getLabel(@PathVariable Long id) {
        return ApiResponse.ok(spectrumService.getLabel(id));
    }

    @PostMapping("/validate")
    public ApiResponse<ValidateResponse> validate(@Valid @RequestBody ValidateRequest request) {
        QcResult result = spectrumService.validateBands(request.getBands());
        ValidateResponse response = new ValidateResponse();
        response.setValid(result.isValid());
        response.setQcStatus(result.getQcStatus());
        response.setErrors(result.getErrors());
        response.setWarnings(result.getWarnings());
        return ApiResponse.ok(response);
    }

    @PostMapping("/import")
    public ApiResponse<ImportResponse> importFile(@RequestParam("file") MultipartFile file,
                                                  @RequestParam(value = "siteId", required = false) Long siteId) {
        return ApiResponse.ok(importService.importFile(file, siteId));
    }
}
