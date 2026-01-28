package com.sludge_system.validation;

import com.sludge_system.domain.QcStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpectrumValidator {
    public static final int EXPECTED_BANDS = 228;
    private static final double LOW_VARIANCE_EPS = 1e-12;
    private static final double EXTREME_VALUE = 1e6;

    public QcResult validate(List<Double> bands) {
        QcResult result = new QcResult();

        if (bands == null) {
            result.getErrors().add("BANDS_LENGTH_INVALID");
            result.setValid(false);
            result.setQcStatus(QcStatus.FAIL);
            result.setQcMessage("BANDS_LENGTH_INVALID");
            return result;
        }

        int size = bands.size();
        if (size != EXPECTED_BANDS) {
            result.getErrors().add("BANDS_LENGTH_INVALID");
        }

        boolean allZero = true;
        double mean = 0.0;
        double m2 = 0.0;
        int count = 0;

        for (Double value : bands) {
            if (value == null || value.isNaN() || value.isInfinite()) {
                result.getErrors().add("BANDS_CONTAINS_NAN");
                break;
            }
            if (value != 0.0) {
                allZero = false;
            }
            count++;
            double delta = value - mean;
            mean += delta / count;
            double delta2 = value - mean;
            m2 += delta * delta2;
            if (value < 0) {
                result.getWarnings().add("NEGATIVE_VALUE");
            } else if (value > EXTREME_VALUE) {
                result.getWarnings().add("EXTREME_VALUE");
            }
        }

        if (count > 1) {
            double variance = m2 / (count - 1);
            if (variance < LOW_VARIANCE_EPS) {
                result.getWarnings().add("LOW_VARIANCE");
            }
        }

        if (allZero && !bands.isEmpty()) {
            result.getWarnings().add("ALL_ZERO");
        }

        if (!result.getErrors().isEmpty()) {
            result.setValid(false);
            result.setQcStatus(QcStatus.FAIL);
        } else if (!result.getWarnings().isEmpty()) {
            result.setValid(true);
            result.setQcStatus(QcStatus.WARN);
        } else {
            result.setValid(true);
            result.setQcStatus(QcStatus.PASS);
        }

        StringBuilder message = new StringBuilder();
        if (!result.getErrors().isEmpty()) {
            message.append(String.join(";", result.getErrors()));
        }
        if (!result.getWarnings().isEmpty()) {
            if (message.length() > 0) {
                message.append(";");
            }
            message.append(String.join(";", result.getWarnings()));
        }
        result.setQcMessage(message.toString());
        return result;
    }
}
