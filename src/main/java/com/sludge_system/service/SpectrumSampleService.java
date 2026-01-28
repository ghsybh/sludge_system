package com.sludge_system.service;

import com.sludge_system.common.ApiException;
import com.sludge_system.common.ErrorCode;
import com.sludge_system.domain.ChemLabel;
import com.sludge_system.domain.QcStatus;
import com.sludge_system.domain.SamplingSite;
import com.sludge_system.domain.SpectrumSample;
import com.sludge_system.dto.ChemLabelRequest;
import com.sludge_system.dto.ChemLabelResponse;
import com.sludge_system.dto.SpectrumCreateRequest;
import com.sludge_system.dto.SpectrumDetailResponse;
import com.sludge_system.dto.SpectrumResponse;
import com.sludge_system.dto.SpectrumUpdateRequest;
import com.sludge_system.repository.ChemLabelRepository;
import com.sludge_system.repository.SamplingSiteRepository;
import com.sludge_system.repository.SpectrumSampleRepository;
import com.sludge_system.validation.QcResult;
import com.sludge_system.validation.SpectrumValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpectrumSampleService {
    private final SpectrumSampleRepository sampleRepository;
    private final SamplingSiteRepository siteRepository;
    private final ChemLabelRepository labelRepository;
    private final SpectrumValidator spectrumValidator;

    public SpectrumSampleService(SpectrumSampleRepository sampleRepository,
                                 SamplingSiteRepository siteRepository,
                                 ChemLabelRepository labelRepository,
                                 SpectrumValidator spectrumValidator) {
        this.sampleRepository = sampleRepository;
        this.siteRepository = siteRepository;
        this.labelRepository = labelRepository;
        this.spectrumValidator = spectrumValidator;
    }

    @Transactional
    public SpectrumResponse create(SpectrumCreateRequest request) {
        SamplingSite site = null;
        if (request.getSiteId() != null) {
            site = siteRepository.findById(request.getSiteId())
                    .orElseThrow(() -> new ApiException(ErrorCode.SITE_NOT_FOUND));
        }

        QcResult qcResult = spectrumValidator.validate(request.getBands());

        SpectrumSample sample = new SpectrumSample();
        sample.setSite(site);
        sample.setBands(request.getBands());
        sample.setBandCount(request.getBands() == null ? 0 : request.getBands().size());
        sample.setSource(request.getSource());
        sample.setCapturedAt(request.getCapturedAt());
        sample.setQcStatus(qcResult.getQcStatus());
        sample.setQcMessage(qcResult.getQcMessage());

        SpectrumSample saved = sampleRepository.save(sample);

        if (request.getLabel() != null) {
            upsertLabel(saved, request.getLabel());
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<SpectrumResponse> list(Long siteId, QcStatus qcStatus, Pageable pageable) {
        if (siteId != null && qcStatus != null) {
            return sampleRepository.findBySiteIdAndQcStatus(siteId, qcStatus, pageable).map(this::toResponse);
        }
        if (siteId != null) {
            return sampleRepository.findBySiteId(siteId, pageable).map(this::toResponse);
        }
        if (qcStatus != null) {
            return sampleRepository.findByQcStatus(qcStatus, pageable).map(this::toResponse);
        }
        return sampleRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public SpectrumDetailResponse getDetail(Long id) {
        SpectrumSample sample = sampleRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.SPECTRUM_NOT_FOUND));
        SpectrumDetailResponse response = toDetailResponse(sample);
        labelRepository.findBySampleId(id).ifPresent(label -> response.setLabel(toLabelResponse(label)));
        return response;
    }

    @Transactional
    public SpectrumResponse update(Long id, SpectrumUpdateRequest request) {
        SpectrumSample sample = sampleRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.SPECTRUM_NOT_FOUND));

        if (request.getSiteId() != null) {
            SamplingSite site = siteRepository.findById(request.getSiteId())
                    .orElseThrow(() -> new ApiException(ErrorCode.SITE_NOT_FOUND));
            sample.setSite(site);
        }
        if (request.getBands() != null) {
            QcResult qcResult = spectrumValidator.validate(request.getBands());
            sample.setBands(request.getBands());
            sample.setBandCount(request.getBands().size());
            sample.setQcStatus(qcResult.getQcStatus());
            sample.setQcMessage(qcResult.getQcMessage());
        }
        if (request.getSource() != null) {
            sample.setSource(request.getSource());
        }
        if (request.getCapturedAt() != null) {
            sample.setCapturedAt(request.getCapturedAt());
        }

        return toResponse(sampleRepository.save(sample));
    }

    @Transactional
    public void delete(Long id) {
        SpectrumSample sample = sampleRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.SPECTRUM_NOT_FOUND));
        labelRepository.findBySampleId(id).ifPresent(labelRepository::delete);
        sampleRepository.delete(sample);
    }

    @Transactional
    public ChemLabelResponse upsertLabel(Long sampleId, ChemLabelRequest request) {
        SpectrumSample sample = sampleRepository.findById(sampleId)
                .orElseThrow(() -> new ApiException(ErrorCode.SPECTRUM_NOT_FOUND));
        return toLabelResponse(upsertLabel(sample, request));
    }

    @Transactional(readOnly = true)
    public ChemLabelResponse getLabel(Long sampleId) {
        ChemLabel label = labelRepository.findBySampleId(sampleId)
                .orElseThrow(() -> new ApiException(ErrorCode.SPECTRUM_NOT_FOUND));
        return toLabelResponse(label);
    }

    public QcResult validateBands(java.util.List<Double> bands) {
        return spectrumValidator.validate(bands);
    }

    private ChemLabel upsertLabel(SpectrumSample sample, ChemLabelRequest request) {
        ChemLabel label = labelRepository.findBySampleId(sample.getId()).orElseGet(ChemLabel::new);
        label.setSample(sample);
        label.setOrganic(request.getOrganic());
        label.setTn(request.getTn());
        label.setTp(request.getTp());
        label.setUnit(request.getUnit());
        return labelRepository.save(label);
    }

    private SpectrumResponse toResponse(SpectrumSample sample) {
        SpectrumResponse response = new SpectrumResponse();
        response.setId(sample.getId());
        response.setSiteId(sample.getSite() != null ? sample.getSite().getId() : null);
        response.setSiteCode(sample.getSite() != null ? sample.getSite().getSiteCode() : null);
        response.setBandCount(sample.getBandCount());
        response.setSource(sample.getSource());
        response.setCapturedAt(sample.getCapturedAt());
        response.setQcStatus(sample.getQcStatus());
        response.setQcMessage(sample.getQcMessage());
        response.setCreatedAt(sample.getCreatedAt());
        response.setUpdatedAt(sample.getUpdatedAt());
        return response;
    }

    private SpectrumDetailResponse toDetailResponse(SpectrumSample sample) {
        SpectrumDetailResponse response = new SpectrumDetailResponse();
        response.setId(sample.getId());
        response.setSiteId(sample.getSite() != null ? sample.getSite().getId() : null);
        response.setSiteCode(sample.getSite() != null ? sample.getSite().getSiteCode() : null);
        response.setSiteName(sample.getSite() != null ? sample.getSite().getSiteName() : null);
        response.setBands(sample.getBands());
        response.setBandCount(sample.getBandCount());
        response.setSource(sample.getSource());
        response.setCapturedAt(sample.getCapturedAt());
        response.setQcStatus(sample.getQcStatus());
        response.setQcMessage(sample.getQcMessage());
        response.setCreatedAt(sample.getCreatedAt());
        response.setUpdatedAt(sample.getUpdatedAt());
        return response;
    }

    private ChemLabelResponse toLabelResponse(ChemLabel label) {
        ChemLabelResponse response = new ChemLabelResponse();
        response.setId(label.getId());
        response.setSampleId(label.getSample().getId());
        response.setOrganic(label.getOrganic());
        response.setTn(label.getTn());
        response.setTp(label.getTp());
        response.setUnit(label.getUnit());
        response.setCreatedAt(label.getCreatedAt());
        response.setUpdatedAt(label.getUpdatedAt());
        return response;
    }
}
