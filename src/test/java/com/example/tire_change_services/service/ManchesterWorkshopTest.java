package com.example.tire_change_services.service;

import com.example.tire_change_services.config.WorkshopInfo;
import com.example.tire_change_services.model.AvailableTime;
import com.example.tire_change_services.model.ContactInformationRequestBody;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ManchesterWorkshopTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WorkshopInfo workshopInfo;  // Mock your WorkshopInfo class if necessary

    @InjectMocks
    private ManchesterWorkshop manchesterWorkshop;  // Replace with the actual service class containing getAvailableTimes

    private Gson gson = new Gson();

    @BeforeEach
    public void setUp() {
        when(workshopInfo.getUrl()).thenReturn("http://test-url.com");
    }

    @Test
    public void testGetAvailableTimes_Success() {
        // Define test inputs
        String from = "2024-07-26";
        String until = "2024-07-27";
        String workshopName = "manchester";
        String carType = "passenger car";

        // Mock JSON response
        String jsonResponse = "[{\"available\": true, \"startTime\": \"10:00\", \"endTime\": \"11:00\"}]";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(responseEntity);

        // Call the method under test
        List<AvailableTime> availableTimes = manchesterWorkshop.getAvailableTimes(from, until, workshopName, carType);

        // Assertions
        assertNotNull(availableTimes);
        assertEquals(1, availableTimes.size());
//        assertEquals("10:00", availableTimes.get(0).getStartTime());
//        assertEquals("11:00", availableTimes.get(0).getEndTime());
    }

    @Test
    public void testGetAvailableTimes_EmptyResponse() {
        // Define test inputs
        String from = "2024-07-26";
        String until = "2024-07-27";
        String workshopName = "manchester";
        String carType = "passenger car";

        // Mock JSON response
        String jsonResponse = "[]";  // Empty list response
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(responseEntity);

        // Call the method under test
        List<AvailableTime> availableTimes = manchesterWorkshop.getAvailableTimes(from, until, workshopName, carType);

        // Assertions
        assertNotNull(availableTimes);
        assertTrue(availableTimes.isEmpty());
    }

    @Test
    public void testGetAvailableTimes_Exception() {
        // Define test inputs
        String from = "2024-07-26";
        String until = "2024-07-27";
        String workshopName = "manchester";
        String carType = "passenger car";

        // Mock an exception
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        // Call the method and expect an exception
        assertThrows(RuntimeException.class, () -> {
            manchesterWorkshop.getAvailableTimes(from, until, workshopName, carType);
        });
    }

    @Test
    public void testBookTime_Success() {
        // Define test inputs
        String id = "123";
        String contactInformation = "contact@example.com";
        String url = String.format("%s/api/v2/tire-change-times/%s/booking", "http://test-url.com", id);

        // Mock the REST response
        String jsonResponse = "{\"available\": true, \"startTime\": \"10:00\", \"endTime\": \"11:00\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(eq(url), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Call the method under test
        AvailableTime result = manchesterWorkshop.bookTime(id, contactInformation);

        // Assertions
//        assertNotNull(result);
//        assertEquals("10:00", result.getStartTime());
//        assertEquals("11:00", result.getEndTime());
//        assertTrue(result.isAvailable());

        // Verify the request entity
        ArgumentCaptor<HttpEntity<ContactInformationRequestBody>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(eq(url), captor.capture(), eq(String.class));
        HttpEntity<ContactInformationRequestBody> requestEntity = captor.getValue();
        assertEquals(MediaType.APPLICATION_JSON, requestEntity.getHeaders().getContentType());
        assertEquals(contactInformation, requestEntity.getBody().getContactInformation());
    }

    @Test
    public void testBookTime_ExceptionHandling() {
        // Define test inputs
        String id = "123";
        String contactInformation = "contact@example.com";
        String url = String.format("%s/api/v2/tire-change-times/%s/booking", "http://test-url.com", id);

        // Mock the REST response to throw an exception
        when(restTemplate.postForEntity(eq(url), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        // Call the method and expect an exception
        assertThrows(RuntimeException.class, () -> {
            manchesterWorkshop.bookTime(id, contactInformation);
        });
    }
}



