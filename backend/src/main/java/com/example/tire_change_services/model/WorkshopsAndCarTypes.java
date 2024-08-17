package com.example.tire_change_services.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class WorkshopsAndCarTypes {
    private Set<NameValue> workshops;
    private Set<NameValue> carTypes;
}
