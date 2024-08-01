package com.example.tire_change_services.model;

import lombok.Value;

@Value
public class AvailableTimeJSON {
    int id;
    String time;
    boolean available;
}
