package com.example.tire_change_services.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkshopError {
    @JacksonXmlProperty(localName = "statusCode")
    private String code;
    @JacksonXmlProperty(localName = "error")
    private String message;
}
