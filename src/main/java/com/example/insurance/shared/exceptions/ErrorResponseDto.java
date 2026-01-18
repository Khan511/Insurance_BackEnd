package com.example.insurance.shared.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;

    private int status;
    private String error;
    private String message;
    private String errorCode;
    private String path;
    private String traceId;
    private String documentationUrl;
    private Map<String, Object> metadata;

    // For validation errors
    private Map<String, String> fieldErrors;
    private List<SubError> subErrors;

    @Data
    @Builder
    public static class SubError {
        private String field;
        private String message;
        private Object rejectedValue;
        private String errorCode;
    }

    // Factory method for consistency
    public static ErrorResponseDto fromErrorCode(ErrorCode errorCode, String path, String traceId) {
        return ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getHttpStatus().value())
                .error(errorCode.getHttpStatus().getReasonPhrase())
                .message(errorCode.getDefaultMessage())
                .errorCode(errorCode.getCode())
                .path(path)
                .traceId(traceId)
                .documentationUrl(buildDocumentationUrl(errorCode))
                .build();
    }

    private static String buildDocumentationUrl(ErrorCode errorCode) {
        return "https://api.insurance.com/docs/errors/" +
                errorCode.getCode().toLowerCase().replace("_", "-");
    }
}