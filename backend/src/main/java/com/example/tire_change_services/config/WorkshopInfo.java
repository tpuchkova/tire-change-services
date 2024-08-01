package com.example.tire_change_services.config;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Getter
@Setter
@Builder
public class WorkshopInfo {
    String name;
    String carTypes;
    String url;
    String address;
}
