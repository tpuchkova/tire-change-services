package com.example.tire_change_services.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
public class WorkshopCommunicationException extends RuntimeException {
    private final HttpStatusCode statusCode;
    private final String errorCode;
    private final String errorMessage;
}
