package com.example.tire_change_services.service;

import com.example.tire_change_services.config.WorkshopInfo;
import com.example.tire_change_services.config.WorkshopsInfoList;
import com.example.tire_change_services.exception.WorkshopCommunicationException;
import com.example.tire_change_services.model.AvailableTime;

import com.example.tire_change_services.model.WorkshopError;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManchesterWorkshopTest {
    private static final String CORRECT_GET_RESPONSE_JSON =
            """
                    [
                        {
                            "id": 1,
                            "time": "2024-07-11T08:00:00Z",
                            "available": true
                        }
                        ]
                    """;
    private static final String CORRECT_POST_RESPONSE_JSON = """
                {
                    "id": 43,
                    "time": "2024-07-17T14:00:00Z",
                    "available": false
                }
                """;
    private static final String ERROR_RESPONSE_JSON = """
            {
                "code": "22",
                "message": "tire change time 207 is unavailable"
            }
            """;
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WorkshopsInfoList workshopsInfoList;

    @InjectMocks
    private ManchesterWorkshop manchesterWorkshop;

    @Test
    void getAvailableTimesTest_shouldSuccessfullyParseResponse() {
        // given
        String dateFrom = "2024-07-26";
        String dateUntil = "2024-07-27";
        String workshopName = "manchester";
        String carType = "passenger car";

        mockRestTemplateGetForEntity(CORRECT_GET_RESPONSE_JSON, HttpStatus.OK);

        mockWorkshopInfoList();

        // when
        List<AvailableTime> availableTimes = manchesterWorkshop.getAvailableTimes(dateFrom, dateUntil, workshopName, carType);

        // then
        assertNotNull(availableTimes);
        assertEquals("1", availableTimes.get(0).getId());
        assertEquals("2024-07-11T08:00:00Z", availableTimes.get(0).getTime());
        assertEquals("manchester", availableTimes.get(0).getWorkshopName());
        assertEquals("address", availableTimes.get(0).getAddress());
        assertEquals("passenger car", availableTimes.get(0).getCarTypes());
    }

    @Test
    void getAvailableTimesTest_ThrowsWorkshopCommunicationException() {
        // given
        String dateFrom = "2024-07-26";
        String dateUntil = "2024-07-27";
        String workshopName = "london";
        String carType = "passenger car";

        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "text", ERROR_RESPONSE_JSON.getBytes(), StandardCharsets.UTF_8));

        mockWorkshopInfoList();

        // when
        WorkshopCommunicationException thrown = assertThrows(
                WorkshopCommunicationException.class,
                () -> manchesterWorkshop.getAvailableTimes(dateFrom, dateUntil, workshopName, carType),
                "Expected HttpClientErrorException to throw, but it didn't"
        );

        // then
        assertEquals("tire change time 207 is unavailable", thrown.getMessage());
        assertEquals("22", thrown.getErrorCode());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, thrown.getStatusCode());
    }

    @Test
    public void bookTimeTest_shouldSuccessfullyParseResponse() {
        // given
        String id = "43";
        String contactInformation = "contact@example.com";

        mockRestTemplatePostForEntity(CORRECT_POST_RESPONSE_JSON, HttpStatus.OK);

        mockWorkshopInfoList();

        // when
        AvailableTime availableTime = manchesterWorkshop.bookTime(id, contactInformation);

        // then
        assertNotNull(availableTime);
        assertEquals("43", availableTime.getId());
        assertEquals("2024-07-17T14:00:00Z", availableTime.getTime());
        assertEquals("manchester", availableTime.getWorkshopName());
        assertEquals("address", availableTime.getAddress());
        assertEquals("passenger car", availableTime.getCarTypes());
    }

    @Test
    void bookTimeTest_ThrowsWorkshopCommunicationException() {
        // given
        String id = "43";
        String contactInformation = "contact@example.com";

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "text", ERROR_RESPONSE_JSON.getBytes(), StandardCharsets.UTF_8));

        mockWorkshopInfoList();

        // when
        WorkshopCommunicationException thrown = assertThrows(
                WorkshopCommunicationException.class,
                () -> manchesterWorkshop.bookTime(id, contactInformation),
                "Expected HttpClientErrorException to throw, but it didn't"
        );

        // then
        assertEquals("tire change time 207 is unavailable", thrown.getMessage());
        assertEquals("22", thrown.getErrorCode());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, thrown.getStatusCode());
    }

    private void mockRestTemplateGetForEntity(String response, HttpStatus status) {
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(response, status));
    }

    private void mockRestTemplatePostForEntity(String response, HttpStatus status) {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(response, status));
    }

    private void mockWorkshopInfoList() {
        when(workshopsInfoList.getWorkshops()).thenReturn(Maps.newHashMap("manchester",
                WorkshopInfo.builder()
                        .url("correctUrl")
                        .name("manchester")
                        .address("address")
                        .carTypes("passenger car")
                        .build()));
    }
}



