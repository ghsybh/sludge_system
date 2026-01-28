package com.sludge_system.service;

import com.sludge_system.common.ApiException;
import com.sludge_system.common.ErrorCode;
import com.sludge_system.domain.PredictionHistory;
import com.sludge_system.domain.SamplingSite;
import com.sludge_system.domain.SpectrumSample;
import com.sludge_system.dto.PredictRequest;
import com.sludge_system.dto.PredictResponse;
import com.sludge_system.dto.PredictionHistoryResponse;
import com.sludge_system.repository.PredictionHistoryRepository;
import com.sludge_system.repository.SamplingSiteRepository;
import com.sludge_system.repository.SpectrumSampleRepository;
import com.sludge_system.validation.QcResult;
import com.sludge_system.validation.SpectrumValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PredictionService {
    private final PredictionHistoryRepository historyRepository;
    private final SpectrumSampleRepository sampleRepository;
    private final SamplingSiteRepository siteRepository;
    private final SpectrumValidator spectrumValidator;

    public PredictionService(PredictionHistoryRepository historyRepository,
                             SpectrumSampleRepository sampleRepository,
                             SamplingSiteRepository siteRepository,
                             SpectrumValidator spectrumValidator) {
        this.historyRepository = historyRepository;
        this.sampleRepository = sampleRepository;
        this.siteRepository = siteRepository;
        this.spectrumValidator = spectrumValidator;
    }

    @Transactional
    public PredictResponse predict(PredictRequest request) {
        SpectrumSample inputSample = null;
        List<Double> bands = request.getBands();
        if (request.getSampleId() != null) {
            inputSample = sampleRepository.findById(request.getSampleId())
                    .orElseThrow(() -> new ApiException(ErrorCode.SPECTRUM_NOT_FOUND));
            bands = inputSample.getBands();
        }

        if (bands != null) {
            QcResult qcResult = spectrumValidator.validate(bands);
            if (!qcResult.isValid()) {
                if (qcResult.getErrors().contains("BANDS_CONTAINS_NAN")) {
                    throw new ApiException(ErrorCode.BANDS_CONTAINS_NAN);
                }
                Map<String, Object> data = new HashMap<>();
                data.put("expected", SpectrumValidator.EXPECTED_BANDS);
                data.put("actual", bands == null ? 0 : bands.size());
                throw new ApiException(ErrorCode.BANDS_LENGTH_INVALID, data);
            }
        }

        PredictResponse response = mockPredict(request.getTaskType(), inputSample);

        PredictionHistory history = new PredictionHistory();
        history.setTaskType(request.getTaskType());
        history.setModelType(request.getModel());
        history.setInputSample(inputSample);
        history.setInputBands(bands);
        history.setPredSiteCode(response.getClassification() != null ? response.getClassification().getSiteCode() : null);
        history.setPredOrganic(response.getRegression() != null ? response.getRegression().getOrganic() : null);
        history.setPredTn(response.getRegression() != null ? response.getRegression().getTn() : null);
        history.setPredTp(response.getRegression() != null ? response.getRegression().getTp() : null);
        history.setMetrics(response.getMetrics());

        if (response.getClassification() != null) {
            SamplingSite site = siteRepository.findBySiteCodeContainingIgnoreCaseOrSiteNameContainingIgnoreCase(
                    response.getClassification().getSiteCode(),
                    response.getClassification().getSiteCode(),
                    Pageable.ofSize(1)
            ).stream().findFirst().orElse(null);
            history.setPredSite(site);
            if (site != null) {
                history.setPredSiteCode(site.getSiteCode());
            }
        }

        historyRepository.save(history);
        return response;
    }

    @Transactional(readOnly = true)
    public Page<PredictionHistoryResponse> list(String taskType, Pageable pageable) {
        if (taskType == null || taskType.isBlank()) {
            return historyRepository.findAll(pageable).map(this::toResponse);
        }
        return historyRepository.findByTaskType(taskType, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PredictionHistoryResponse get(Long id) {
        PredictionHistory history = historyRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.PREDICTION_NOT_FOUND));
        return toResponse(history);
    }

    @Transactional
    public void delete(Long id) {
        PredictionHistory history = historyRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.PREDICTION_NOT_FOUND));
        historyRepository.delete(history);
    }

    private PredictResponse mockPredict(String taskType, SpectrumSample inputSample) {
        PredictResponse response = new PredictResponse();
        boolean doClassification = "both".equalsIgnoreCase(taskType) || "classification".equalsIgnoreCase(taskType);
        boolean doRegression = "both".equalsIgnoreCase(taskType) || "regression".equalsIgnoreCase(taskType);

        if (doClassification) {
            PredictResponse.ClassificationResult classification = new PredictResponse.ClassificationResult();
            if (inputSample != null && inputSample.getSite() != null) {
                classification.setSiteCode(inputSample.getSite().getSiteCode());
            } else {
                classification.setSiteCode("S000");
            }
            classification.setConfidence(0.82);
            response.setClassification(classification);
        }

        if (doRegression) {
            PredictResponse.RegressionResult regression = new PredictResponse.RegressionResult();
            regression.setOrganic(12.3);
            regression.setTn(1.02);
            regression.setTp(0.33);
            regression.setUnit("g/kg");
            response.setRegression(regression);
        }

        response.setMetrics(new HashMap<>());
        return response;
    }

    private PredictionHistoryResponse toResponse(PredictionHistory history) {
        PredictionHistoryResponse response = new PredictionHistoryResponse();
        response.setId(history.getId());
        response.setTaskType(history.getTaskType());
        response.setModelType(history.getModelType());
        response.setInputSampleId(history.getInputSample() != null ? history.getInputSample().getId() : null);
        response.setInputBands(history.getInputBands());
        response.setPredSiteId(history.getPredSite() != null ? history.getPredSite().getId() : null);
        response.setPredSiteCode(history.getPredSiteCode());
        response.setPredOrganic(history.getPredOrganic());
        response.setPredTn(history.getPredTn());
        response.setPredTp(history.getPredTp());
        response.setMetrics(history.getMetrics());
        response.setCreatedAt(history.getCreatedAt());
        return response;
    }
}
