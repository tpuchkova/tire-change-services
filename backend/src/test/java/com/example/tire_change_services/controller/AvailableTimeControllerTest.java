package com.example.tire_change_services.controller;

import com.example.tire_change_services.model.AvailableTime;
import com.example.tire_change_services.service.WorkshopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class AvailableTimeControllerTest {
    @Mock
    private WorkshopService workshopService;

    @InjectMocks
    private AvailableTimeController workshopController; // Assuming this is the controller class name

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAvailableTimesTest() {
        // Arrange
        String from = "2024-08-01";
        String until = "2024-08-10";
        String workshopName = "london";
        String carType = "passenger car";

        List<AvailableTime> mockAvailableTimes = new ArrayList<>();
        mockAvailableTimes.add(
                AvailableTime.builder()
                        .id("1")
                        .time("2024-08-02T10:00:00")
                        .workshopName("london")
                        .address("address")
                        .carTypes("passengerCar")
                        .build());
        mockAvailableTimes.add(
                AvailableTime.builder()
                        .id("2")
                        .time("2024-08-03T10:00:00")
                        .workshopName("london")
                        .address("address")
                        .carTypes("passengerCar")
                        .build());

        when(workshopService.getAvailableTimes(from, until, workshopName, carType))
                .thenReturn(mockAvailableTimes);

        // Act
        ResponseEntity<List<AvailableTime>> response = workshopController.getAvailableTimes(from, until, workshopName, carType);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(mockAvailableTimes, response.getBody());
    }

    @Test
    public void bookAvailableTimeByIdTest() {
        // Arrange
        String id = "1";
        String workshopName = "london";
        String contactInformation = "contact@example.com";

        AvailableTime mockAvailableTime = AvailableTime.builder()
                .id("1")
                .time("2024-08-03T10:00:00Z")
                .workshopName("london")
                .address("address")
                .carTypes("passengerCar")
                .build();
        when(workshopService.bookTime(id, workshopName, contactInformation)).thenReturn(mockAvailableTime);

        // Act
        ResponseEntity<AvailableTime> response = workshopController.bookAvailableTimeById(id, workshopName, contactInformation);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockAvailableTime, response.getBody());
    }
}
