package com.example.tire_change_services.service;

import com.example.tire_change_services.config.WorkshopInfo;
import com.example.tire_change_services.config.WorkshopsInfoList;
import com.example.tire_change_services.model.AvailableTime;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WorkshopServiceTest {
    @Mock
    private IWorkshop london;

    @Mock
    private IWorkshop manchester;
    @Mock
    private List<IWorkshop> workshops;
    @InjectMocks
    private WorkshopService workshopService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        workshops = Arrays.asList(london, manchester);
        workshopService = new WorkshopService(workshops); // Assuming WorkshopService is the name of your service class
    }

    @Test
    public void getAvailableTimesTest_WithWorkshopNameAndCarType() {
        // Arrange
        String from = "2024-08-01";
        String until = "2024-08-10";
        String workshopName = "london";
        String carType = "passenger car";

        WorkshopInfo londonInfo = WorkshopInfo.builder()
                .url("correctUrl")
                .name("london")
                .address("address")
                .carTypes("passenger car")
                .build();

        WorkshopInfo manchesterInfo = WorkshopInfo.builder()
                .url("correctUrl")
                .name("manchester")
                .address("address")
                .carTypes("passenger car")
                .build();

        when(london.getWorkshopInfo()).thenReturn(londonInfo);
        when(manchester.getWorkshopInfo()).thenReturn(manchesterInfo);

        AvailableTime availableTime =  AvailableTime.builder()
                .id("1")
                .time("2024-08-03T10:00:00Z")
                .workshopName("london")
                .address("address")
                .carTypes("passenger car")
                .build();

        when(london.getAvailableTimes(from, until, workshopName, carType))
                .thenReturn(Arrays.asList(availableTime));
        when(manchester.getAvailableTimes(from, until, workshopName, carType))
                .thenReturn(new ArrayList<>());

        // Act
        List<AvailableTime> result = workshopService.getAvailableTimes(from, until, workshopName, carType);

        // Assert
        assertEquals(1, result.size());
        assertEquals(availableTime, result.get(0));
    }

    @Test
    public void getAvailableTimesTest_WithDateFiltering() {
        // Arrange
        String from = "2024-08-01";
        String until = "2024-08-10";
        String workshopName = "";
        String carType = "";

        WorkshopInfo workshopInfo = WorkshopInfo.builder()
                        .url("correctUrl")
                        .name("london")
                        .address("address")
                        .carTypes("passenger car")
                        .build();

        AvailableTime availableTime1 = AvailableTime.builder()
                .id("1")
                .time("2024-08-03T10:00:00Z")
                .workshopName("london")
                .address("address")
                .carTypes("passengerCar")
                .build();
        AvailableTime availableTime2 = AvailableTime.builder()
                .id("2")
                .time("2024-08-11T10:00:00Z")
                .workshopName("manchester")
                .address("address")
                .carTypes("passengerCar")
                .build();

        when(london.getWorkshopInfo()).thenReturn(workshopInfo);
        when(london.getAvailableTimes(from, until, workshopName, carType))
                .thenReturn(Arrays.asList(availableTime1, availableTime2));

        // Act
        List<AvailableTime> result = workshopService.getAvailableTimes(from, until, workshopName, carType);

        // Assert
        assertEquals(1, result.size());
        assertEquals(availableTime1, result.get(0));
    }

    @Test
    public void bookTimeTest_Success() {
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

        WorkshopInfo workshopInfo = WorkshopInfo.builder()
                .url("correctUrl")
                .name("london")
                .address("address")
                .carTypes("passenger car")
                .build();

        when(london.getWorkshopInfo()).thenReturn(workshopInfo);
        when(london.bookTime(id, contactInformation)).thenReturn(mockAvailableTime);

        // Act
        AvailableTime result = workshopService.bookTime(id, workshopName, contactInformation);

        // Assert
        assertEquals(mockAvailableTime, result);
    }
}
