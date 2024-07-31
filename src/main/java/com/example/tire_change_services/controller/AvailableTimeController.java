package com.example.tire_change_services.controller;

import com.example.tire_change_services.model.AvailableTime;
import com.example.tire_change_services.model.AvailableTimeXml;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.tire_change_services.service.WorkshopService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AvailableTimeController {
    private final WorkshopService workshopService;

    @GetMapping("/getAvailableTimes")
    public ResponseEntity<List<AvailableTime>> getAvailableTimes(@RequestParam String from, @RequestParam String until, @RequestParam(required = false) String workshopName, @RequestParam(required = false) String carType) {
       //TODO implement
        List<AvailableTime> availableTimes = workshopService.getAvailableTimes(from, until, workshopName, carType);

        return new ResponseEntity<>(availableTimes, HttpStatus.OK);
    }

    @PostMapping("/bookAvailableTime")
    public ResponseEntity<AvailableTime> bookAvailableTimeById(@RequestParam String id, @RequestParam String workshopName, @RequestBody String contactInformation) {
        AvailableTime availableTime = workshopService.bookTime(id, workshopName, contactInformation);
        return new ResponseEntity<>(availableTime, HttpStatus.OK);
    }
}
