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
import org.mockito.junit.jupiter.MockitoExtension;;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LondonWorkshopTest {
    private static final String CORRECT_GET_RESPONSE_XML =
            """
                <tireChangeTimesResponse>
                    <availableTime>
                        <uuid>68f2cacb-a591-4e6c-96af-2d4854372ce6</uuid>
                        <time>2024-07-11T11:00:00Z</time>
                    </availableTime>
                </tireChangeTimesResponse>
            """;
    private static final String CORRECT_PUT_RESPONSE_XML = """
                <tireChangeBookingResponse>
                    <uuid>3ea08261-2132-4fc3-8659-84a1daab7223</uuid>
                    <time>2024-08-12T15:00:00Z</time>
                </tireChangeBookingResponse>
                """;
    private static final String ERROR_RESPONSE_XML = """
            <errorResponse>
                <statusCode>422</statusCode>
                <error>tire change time  is unavailable</error>
            </errorResponse>
            """;
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WorkshopsInfoList workshopsInfoList;

    @InjectMocks
    private LondonWorkshop londonWorkshop;

    @Test
    void getAvailableTimesTest_shouldSuccessfullyParseResponse() {
        // given
        String dateFrom = "2024-07-26";
        String dateUntil = "2024-07-27";
        String workshopName = "london";
        String carType = "passenger car";

        mockRestTemplateGetForEntity(CORRECT_GET_RESPONSE_XML, HttpStatus.OK);

        mockWorkshopInfoList();

        // when
        List<AvailableTime> availableTimes = londonWorkshop.getAvailableTimes(dateFrom, dateUntil, workshopName, carType);

        // then
        assertNotNull(availableTimes);
        assertEquals("68f2cacb-a591-4e6c-96af-2d4854372ce6", availableTimes.get(0).getId());
        assertEquals("2024-07-11T11:00:00Z", availableTimes.get(0).getTime());
        assertEquals("london", availableTimes.get(0).getWorkshopName());
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
                .thenThrow(new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "text", ERROR_RESPONSE_XML.getBytes(), StandardCharsets.UTF_8));

        mockWorkshopInfoList();

        // when
        WorkshopCommunicationException thrown = assertThrows(
                WorkshopCommunicationException.class,
                () -> londonWorkshop.getAvailableTimes(dateFrom, dateUntil, workshopName, carType),
                "Expected HttpClientErrorException to throw, but it didn't"
        );

        // then
        assertEquals("tire change time  is unavailable", thrown.getMessage());
        assertEquals("422", thrown.getErrorCode());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, thrown.getStatusCode());
    }

    @Test
    public void bookTimeTest_shouldSuccessfullyParseResponse() {
        // given
        String id = "3ea08261-2132-4fc3-8659-84a1daab7223";
        String contactInformation = "contact@example.com";

        mockRestTemplateExchange(CORRECT_PUT_RESPONSE_XML, HttpStatus.OK);

        mockWorkshopInfoList();

        // when
        AvailableTime availableTime = londonWorkshop.bookTime(id, contactInformation);

        // then
        assertNotNull(availableTime);
        assertEquals("3ea08261-2132-4fc3-8659-84a1daab7223", availableTime.getId());
        assertEquals("2024-08-12T15:00:00Z", availableTime.getTime());
        assertEquals("london", availableTime.getWorkshopName());
        assertEquals("address", availableTime.getAddress());
        assertEquals("passenger car", availableTime.getCarTypes());
    }

    @Test
    void bookTimeTest_ThrowsWorkshopCommunicationException() {
        // given
        String id = "3ea08261-2132-4fc3-8659-84a1daab7223";
        String contactInformation = "contact@example.com";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "text", ERROR_RESPONSE_XML.getBytes(), StandardCharsets.UTF_8));

        mockWorkshopInfoList();

        // when
        WorkshopCommunicationException thrown = assertThrows(
                WorkshopCommunicationException.class,
                () -> londonWorkshop.bookTime(id, contactInformation),
                "Expected HttpClientErrorException to throw, but it didn't"
        );

        // then
        assertEquals("tire change time  is unavailable", thrown.getMessage());
        assertEquals("422", thrown.getErrorCode());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, thrown.getStatusCode());
    }

    private void mockWorkshopInfoList() {
        when(workshopsInfoList.getWorkshops()).thenReturn(Maps.newHashMap("london",
                WorkshopInfo.builder()
                        .url("url")
                        .name("london")
                        .address("address")
                        .carTypes("passenger car")
                        .build()));
    }

    private void mockRestTemplateExchange(String response, HttpStatus status) {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(response, status));
    }

    private void mockRestTemplateGetForEntity(String response, HttpStatus status) {
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(response, status));
    }
}

