package com.example.tire_change_services.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class WorkshopCommunicationException extends RuntimeException {
    private final HttpStatusCode statusCode;
    private final String errorCode;
    private final String errorMessage;

    public WorkshopCommunicationException(HttpStatusCode statusCode, String errorCode, String errorMessage){
        super(errorMessage);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
