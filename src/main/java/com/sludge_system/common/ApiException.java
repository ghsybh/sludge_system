package com.sludge_system.common;

import java.util.Collections;
import java.util.Map;

public class ApiException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Map<String, Object> data;

    public ApiException(ErrorCode errorCode) {
        this(errorCode, Collections.emptyMap());
    }

    public ApiException(ErrorCode errorCode, Map<String, Object> data) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = data;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
