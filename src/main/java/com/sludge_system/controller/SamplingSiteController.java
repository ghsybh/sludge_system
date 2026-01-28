package com.sludge_system.controller;

import com.sludge_system.common.ApiResponse;
import com.sludge_system.dto.SiteCreateRequest;
import com.sludge_system.dto.SiteResponse;
import com.sludge_system.dto.SiteUpdateRequest;
import com.sludge_system.service.SamplingSiteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sites")
public class SamplingSiteController {
    private final SamplingSiteService siteService;

    public SamplingSiteController(SamplingSiteService siteService) {
        this.siteService = siteService;
    }

    @PostMapping
    public ApiResponse<SiteResponse> create(@Valid @RequestBody SiteCreateRequest request) {
        return ApiResponse.ok(siteService.create(request));
    }

    @GetMapping
    public ApiResponse<Page<SiteResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size,
                                                @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(siteService.list(keyword, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<SiteResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(siteService.get(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<SiteResponse> update(@PathVariable Long id,
                                            @RequestBody SiteUpdateRequest request) {
        return ApiResponse.ok(siteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        siteService.delete(id);
        return ApiResponse.ok(null);
    }
}
