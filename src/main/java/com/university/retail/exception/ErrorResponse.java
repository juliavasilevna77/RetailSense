package com.university.retail.exception;

import lombok.Builder;
import lombok.Value;
import java.time.LocalDateTime;

@Value
@Builder
public class ErrorResponse {
    String message;
    int statusCode;
    LocalDateTime timestamp;
}
