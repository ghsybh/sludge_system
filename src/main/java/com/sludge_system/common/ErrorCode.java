package com.sludge_system.common;

public enum ErrorCode {
    BANDS_LENGTH_INVALID(40001, "BANDS_LENGTH_INVALID"),
    BANDS_CONTAINS_NAN(40002, "BANDS_CONTAINS_NAN"),
    SITE_NOT_FOUND(40003, "SITE_NOT_FOUND"),
    SPECTRUM_NOT_FOUND(40004, "SPECTRUM_NOT_FOUND"),
    IMPORT_FORMAT_INVALID(40005, "IMPORT_FORMAT_INVALID"),
    PREDICTION_NOT_FOUND(40006, "PREDICTION_NOT_FOUND"),
    SITE_HAS_SPECTRA_CANNOT_DELETE(40901, "SITE_HAS_SPECTRA_CANNOT_DELETE"),
    INTERNAL_ERROR(50001, "INTERNAL_ERROR");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
