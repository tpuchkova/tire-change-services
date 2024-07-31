package com.example.tire_change_services.model;

import lombok.*;

@Getter
@Setter
@Builder
public class AvailableTime {
     String id;
     String time;
     String workshopName;
     String address;
     String carTypes;
}
