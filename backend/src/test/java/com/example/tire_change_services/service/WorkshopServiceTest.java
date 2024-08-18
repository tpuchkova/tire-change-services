package com.example.tire_change_services.service;

import com.example.tire_change_services.config.WorkshopInfo;
import com.example.tire_change_services.model.AvailableTime;

import com.example.tire_change_services.model.NameValue;
import com.example.tire_change_services.model.WorkshopsAndCarTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WorkshopServiceTest {
    @Mock
    private IWorkshop london;
    @Mock
    private IWorkshop manchester;

    private WorkshopService workshopService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        List<IWorkshop> workshops = Arrays.asList(london, manchester);
        workshopService = new WorkshopService(workshops);
    }

    @Test
    public void getAvailableTimesTest_WithWorkshopNameAndCarType() {
        // given
        String from = "2024-08-01";
        String until = "2024-08-10";
        String workshopName = "london";
        String carType = "passenger car";

        mockLondonWorkshopInfo();
        mockManchesterWorkshopInfo();

        AvailableTime availableTime = getAvailableTime("1", "2024-08-03T10:00:00Z");

        when(london.getAvailableTimes(from, until, workshopName, carType))
                .thenReturn(Collections.singletonList(availableTime));

        // when
        List<AvailableTime> result = workshopService.getAvailableTimes(from, until, workshopName, carType);

        // then
        assertEquals(1, result.size());
        assertEquals(availableTime, result.get(0));
    }

    @Test
    public void getAvailableTimesTest_WithDateFiltering() {
        // given
        String from = "2024-08-01";
        String until = "2024-08-10";
        String workshopName = "";
        String carType = "";

        AvailableTime availableTime1 = getAvailableTime("1", "2024-08-03T10:00:00Z");
        AvailableTime availableTime2 = getAvailableTime("2", "2024-08-11T10:00:00Z");

        mockLondonWorkshopInfo();

        when(london.getAvailableTimes(from, until, workshopName, carType))
                .thenReturn(Arrays.asList(availableTime1, availableTime2));

        // when
        List<AvailableTime> result = workshopService.getAvailableTimes(from, until, workshopName, carType);

        // then
        assertEquals(1, result.size());
        assertEquals(availableTime1, result.get(0));
    }

    @Test
    public void bookTimeTest_Success() {
        // given
        String id = "1";
        String workshopName = "london";
        String contactInformation = "contact@example.com";

        AvailableTime availableTime = getAvailableTime("1", "2024-08-03T10:00:00Z");

        mockLondonWorkshopInfo();

        when(london.bookTime(id, contactInformation)).thenReturn(availableTime);

        // when
        AvailableTime result = workshopService.bookTime(id, workshopName, contactInformation);

        // then
        assertEquals(availableTime, result);
    }

    @Test
    public void getWorkshopsAndCarTypesTest_Success() {
        // given
        mockLondonWorkshopInfo();
        mockManchesterWorkshopInfo();

        NameValue workshop1 = new NameValue("London", "london");
        NameValue workshop2 = new NameValue("Manchester", "manchester");

        NameValue carType1 = new NameValue("Passenger car", "passenger car");

        Set<NameValue> workshops = new HashSet<>();
        workshops.add(workshop1);
        workshops.add(workshop2);

        Set<NameValue> carTypes = new HashSet<>();
        carTypes.add(carType1);

        // when
        WorkshopsAndCarTypes result = workshopService.getWorkshopsAndCarTypes();

        // then
        assertEquals(workshops, result.getWorkshops());
        assertEquals(carTypes, result.getCarTypes());
    }

    private void mockManchesterWorkshopInfo() {
        when(manchester.getWorkshopInfo()).thenReturn(WorkshopInfo.builder()
                .url("url")
                .name("manchester")
                .address("address")
                .carTypes("passenger car")
                .build());
    }

    private void mockLondonWorkshopInfo() {
        when(london.getWorkshopInfo()).thenReturn(WorkshopInfo.builder()
                .url("url")
                .name("london")
                .address("address")
                .carTypes("passenger car")
                .build());
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
