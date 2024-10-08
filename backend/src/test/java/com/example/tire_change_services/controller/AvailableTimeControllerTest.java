package com.example.tire_change_services.controller;

import com.example.tire_change_services.model.AvailableTime;
import com.example.tire_change_services.model.NameValue;
import com.example.tire_change_services.model.WorkshopsAndCarTypes;
import com.example.tire_change_services.service.WorkshopService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class AvailableTimeControllerTest {
    @Mock
    private WorkshopService workshopService;

    @InjectMocks
    private AvailableTimeController workshopController;

    @Test
    public void getAvailableTimesTest() {
        // given
        String from = "2024-08-01";
        String until = "2024-08-10";
        String workshopName = "london";
        String carType = "passenger car";

        List<AvailableTime> availableTimes = new ArrayList<>();
        availableTimes.add(getAvailableTime("1", "2024-08-02T10:00:00"));
        availableTimes.add(getAvailableTime("2", "2024-08-03T10:00:00"));

        when(workshopService.getAvailableTimes(from, until, workshopName, carType))
                .thenReturn(availableTimes);

        // when
        ResponseEntity<List<AvailableTime>> response = workshopController.getAvailableTimes(from, until, workshopName, carType);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(availableTimes, response.getBody());
    }

    @Test
    public void bookAvailableTimeByIdTest() {
        // given
        String id = "1";
        String workshopName = "london";
        String contactInformation = "contact@example.com";

        AvailableTime availableTime = getAvailableTime("1", "2024-08-02T10:00:00");

        when(workshopService.bookTime(id, workshopName, contactInformation)).thenReturn(availableTime);

        // when
        ResponseEntity<AvailableTime> response = workshopController.bookAvailableTimeById(id, workshopName, contactInformation);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(availableTime, response.getBody());
    }

    @Test
    public void getWorkshopsAndCarTypesTest() {
        // given
        NameValue workshop1 = new NameValue("London", "london");
        NameValue workshop2 = new NameValue("Manchester", "manchester");

        NameValue carType1 = new NameValue("Passenger car", "passenger car");
        NameValue carType2 = new NameValue("Truck", "truck");

        Set<NameValue> workshops = new HashSet<>();
        workshops.add(workshop1);
        workshops.add(workshop2);

        Set<NameValue> carTypes = new HashSet<>();
        carTypes.add(carType1);
        carTypes.add(carType2);

        WorkshopsAndCarTypes workshopsAndCarTypes = new WorkshopsAndCarTypes(workshops, carTypes);

        when(workshopService.getWorkshopsAndCarTypes()).thenReturn(workshopsAndCarTypes);

        // when
        WorkshopsAndCarTypes response = workshopController.getWorkshopsAndCarTypes();

        // then
        assertEquals(workshopsAndCarTypes, response);
    }

    private AvailableTime getAvailableTime(String id, String time) {
        return AvailableTime.builder()
                .id(id)
                .time(time)
                .workshopName("london")
                .address("address")
                .carTypes("passenger car")
                .build();
    }
}
