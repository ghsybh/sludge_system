package com.sludge_system.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sludge_system.common.ApiException;
import com.sludge_system.common.ErrorCode;
import com.sludge_system.dto.ChemLabelRequest;
import com.sludge_system.dto.ImportFailure;
import com.sludge_system.dto.ImportResponse;
import com.sludge_system.dto.SpectrumCreateRequest;
import com.sludge_system.repository.SamplingSiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SpectrumImportService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE_TIME;

    private final SpectrumSampleService sampleService;
    private final SamplingSiteRepository siteRepository;

    public SpectrumImportService(SpectrumSampleService sampleService,
                                 SamplingSiteRepository siteRepository) {
        this.sampleService = sampleService;
        this.siteRepository = siteRepository;
    }

    public ImportResponse importFile(MultipartFile file, Long siteId) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(ErrorCode.IMPORT_FORMAT_INVALID);
        }
        String filename = file.getOriginalFilename();
        if (filename != null && filename.toLowerCase().endsWith(".json")) {
            return importJson(file, siteId);
        }
        return importCsv(file, siteId);
    }

    private ImportResponse importJson(MultipartFile file, Long siteId) {
        try {
            byte[] bytes = file.getBytes();
            List<Map<String, Object>> rows = MAPPER.readValue(bytes, new TypeReference<>() {});
            ImportResponse response = new ImportResponse();
            int index = 0;
            for (Map<String, Object> row : rows) {
                index++;
                try {
                    SpectrumCreateRequest request = fromJsonRow(row, siteId);
                    sampleService.create(request);
                    response.setSuccessCount(response.getSuccessCount() + 1);
                } catch (Exception ex) {
                    response.setFailCount(response.getFailCount() + 1);
                    ImportFailure failure = new ImportFailure();
                    failure.setRow(index);
                    failure.setReason(ex.getMessage());
                    response.getFailures().add(failure);
                }
            }
            return response;
        } catch (IOException ex) {
            throw new ApiException(ErrorCode.IMPORT_FORMAT_INVALID);
        }
    }

    private ImportResponse importCsv(MultipartFile file, Long siteId) {
        ImportResponse response = new ImportResponse();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                row++;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    SpectrumCreateRequest request = fromCsvLine(line, siteId);
                    sampleService.create(request);
                    response.setSuccessCount(response.getSuccessCount() + 1);
                } catch (Exception ex) {
                    response.setFailCount(response.getFailCount() + 1);
                    ImportFailure failure = new ImportFailure();
                    failure.setRow(row);
                    failure.setReason(ex.getMessage());
                    response.getFailures().add(failure);
                }
            }
        } catch (IOException ex) {
            throw new ApiException(ErrorCode.IMPORT_FORMAT_INVALID);
        }
        return response;
    }

    private SpectrumCreateRequest fromJsonRow(Map<String, Object> row, Long siteId) {
        SpectrumCreateRequest request = new SpectrumCreateRequest();
        if (row.containsKey("siteId")) {
            request.setSiteId(asLong(row.get("siteId")));
        } else if (row.containsKey("siteCode")) {
            String siteCode = String.valueOf(row.get("siteCode"));
            var site = siteRepository.findBySiteCode(siteCode);
            request.setSiteId(site != null ? site.getId() : siteId);
        } else {
            request.setSiteId(siteId);
        }
        request.setSource("import");
        request.setCapturedAt(parseDate(row.get("capturedAt")));
        request.setBands(castBands(row.get("bands")));

        if (row.containsKey("organic") || row.containsKey("tn") || row.containsKey("tp")) {
            ChemLabelRequest label = new ChemLabelRequest();
            label.setOrganic(asDouble(row.get("organic")));
            label.setTn(asDouble(row.get("tn")));
            label.setTp(asDouble(row.get("tp")));
            label.setUnit(row.get("unit") != null ? String.valueOf(row.get("unit")) : null);
            request.setLabel(label);
        }
        return request;
    }

    private SpectrumCreateRequest fromCsvLine(String line, Long siteId) {
        String[] parts = line.split(",");
        int index = 0;
        SpectrumCreateRequest request = new SpectrumCreateRequest();

        if (parts.length < 228) {
            throw new ApiException(ErrorCode.IMPORT_FORMAT_INVALID);
        }

        if (parts.length > 230) {
            String siteCode = parts[index++].trim();
            if (!siteCode.isBlank()) {
                var site = siteRepository.findBySiteCode(siteCode);
                if (site != null) {
                    request.setSiteId(site.getId());
                } else {
                    request.setSiteId(siteId);
                }
            } else {
                request.setSiteId(siteId);
            }
            String capturedAtStr = parts[index++].trim();
            request.setCapturedAt(parseDate(capturedAtStr));
        } else {
            request.setSiteId(siteId);
        }

        List<Double> bands = new ArrayList<>();
        for (int i = 0; i < 228 && index < parts.length; i++, index++) {
            bands.add(asDouble(parts[index]));
        }
        request.setBands(bands);
        request.setSource("import");

        if (parts.length >= index + 3) {
            ChemLabelRequest label = new ChemLabelRequest();
            label.setOrganic(asDouble(parts[index++]));
            label.setTn(asDouble(parts[index++]));
            label.setTp(asDouble(parts[index++]));
            request.setLabel(label);
        }
        return request;
    }

    private LocalDateTime parseDate(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        if (text.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(text, ISO);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private List<Double> castBands(Object value) {
        if (value == null) {
            return null;
        }
        return MAPPER.convertValue(value, new TypeReference<List<Double>>() {});
    }

    private Double asDouble(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        if (text.isBlank()) {
            return null;
        }
        return Double.valueOf(text);
    }

    private Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        if (text.isBlank()) {
            return null;
        }
        return Long.valueOf(text);
    }
}
