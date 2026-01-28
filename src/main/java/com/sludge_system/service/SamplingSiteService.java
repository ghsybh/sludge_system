package com.sludge_system.service;

import com.sludge_system.common.ApiException;
import com.sludge_system.common.ErrorCode;
import com.sludge_system.domain.SamplingSite;
import com.sludge_system.dto.SiteCreateRequest;
import com.sludge_system.dto.SiteResponse;
import com.sludge_system.dto.SiteUpdateRequest;
import com.sludge_system.repository.ChemLabelRepository;
import com.sludge_system.repository.PredictionHistoryRepository;
import com.sludge_system.repository.SamplingSiteRepository;
import com.sludge_system.repository.SpectrumSampleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SamplingSiteService {
    private final SamplingSiteRepository siteRepository;
    private final SpectrumSampleRepository spectrumSampleRepository;
    private final ChemLabelRepository labelRepository;
    private final PredictionHistoryRepository historyRepository;

    public SamplingSiteService(SamplingSiteRepository siteRepository,
                               SpectrumSampleRepository spectrumSampleRepository,
                               ChemLabelRepository labelRepository,
                               PredictionHistoryRepository historyRepository) {
        this.siteRepository = siteRepository;
        this.spectrumSampleRepository = spectrumSampleRepository;
        this.labelRepository = labelRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public SiteResponse create(SiteCreateRequest request) {
        SamplingSite site = new SamplingSite();
        site.setSiteCode(request.getSiteCode());
        site.setSiteName(request.getSiteName());
        site.setLat(request.getLat());
        site.setLng(request.getLng());
        site.setRemark(request.getRemark());
        return toResponse(siteRepository.save(site));
    }

    @Transactional(readOnly = true)
    public Page<SiteResponse> list(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return siteRepository.findAll(pageable).map(this::toResponse);
        }
        return siteRepository
                .findBySiteCodeContainingIgnoreCaseOrSiteNameContainingIgnoreCase(keyword, keyword, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public SiteResponse get(Long id) {
        SamplingSite site = siteRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.SITE_NOT_FOUND));
        return toResponse(site);
    }

    @Transactional
    public SiteResponse update(Long id, SiteUpdateRequest request) {
        SamplingSite site = siteRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.SITE_NOT_FOUND));
        if (request.getSiteCode() != null) {
            site.setSiteCode(request.getSiteCode());
        }
        if (request.getSiteName() != null) {
            site.setSiteName(request.getSiteName());
        }
        if (request.getLat() != null) {
            site.setLat(request.getLat());
        }
        if (request.getLng() != null) {
            site.setLng(request.getLng());
        }
        if (request.getRemark() != null) {
            site.setRemark(request.getRemark());
        }
        return toResponse(siteRepository.save(site));
    }

    @Transactional
    public void delete(Long id) {
        SamplingSite site = siteRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.SITE_NOT_FOUND));
        List<Long> sampleIds = spectrumSampleRepository.findIdsBySiteId(site.getId());
        if (!sampleIds.isEmpty()) {
            labelRepository.deleteBySampleIdIn(sampleIds);
            historyRepository.deleteByInputSampleIdIn(sampleIds);
            spectrumSampleRepository.deleteByIdIn(sampleIds);
        }
        historyRepository.deleteByPredSiteId(site.getId());
        siteRepository.delete(site);
    }

    private SiteResponse toResponse(SamplingSite site) {
        SiteResponse response = new SiteResponse();
        response.setId(site.getId());
        response.setSiteCode(site.getSiteCode());
        response.setSiteName(site.getSiteName());
        response.setLat(site.getLat());
        response.setLng(site.getLng());
        response.setRemark(site.getRemark());
        response.setCreatedAt(site.getCreatedAt());
        response.setUpdatedAt(site.getUpdatedAt());
        return response;
    }
}
