package com.example.tire_change_services.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "workshops-info-list")
public class WorkshopsInfoList {
    private Map<String, WorkshopInfo> workshops;
}