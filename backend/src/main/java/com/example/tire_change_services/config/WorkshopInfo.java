package com.example.tire_change_services.config;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WorkshopInfo {
    String name;
    String carTypes;
    String url;
    String address;
}
