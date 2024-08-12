package com.example.tire_change_services.service;

import com.example.tire_change_services.config.WorkshopInfo;
import com.example.tire_change_services.config.WorkshopsInfoList;
import com.example.tire_change_services.model.AvailableTime;
import com.example.tire_change_services.model.ContactInformationRequestBody;
import com.google.gson.Gson;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
public class ManchesterWorkshopTest {
    private static final String CORRECT_RESPONSE_JSON =
            """
                    [
                        {
                            "id": 1,
                            "time": "2024-07-11T08:00:00Z",
                            "available": true
                        }
                        ]
                    """;
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WorkshopsInfoList workshopsInfoList;

    @InjectMocks
    private ManchesterWorkshop manchesterWorkshop;

    // private Gson gson = new Gson();

//    @BeforeEach
//    public void setUp() {
//        when(workshopInfo.getUrl()).thenReturn("http://test-url.com");
//    }

    @Test
    void shouldSuccessfullyParseResponse() {
        // given
        mockRestTemplateGetForEntity(CORRECT_RESPONSE_JSON, HttpStatus.OK);

        when(workshopsInfoList.getWorkshops()).thenReturn(Maps.newHashMap("manchester",
                WorkshopInfo.builder()
                        .url("correctUrl")
                        .name("manchester")
                        .address("address")
                        .carTypes("passenger car")
                        .build()));

        // when
        List<AvailableTime> availableTimes = manchesterWorkshop.getAvailableTimes("2024-07-26", "2024-07-27", "manchester", "passenger car");

        // then
        assertNotNull(availableTimes);
//        assertEquals("london", availableTimes.get(0).getWorkshopName());
//        assertEquals("address", availableTimes.get(0).getAddress());
//        assertEquals("passenger car", availableTimes.get(0).getCarTypes());
    }

    private void mockRestTemplateGetForEntity(String response, HttpStatus status) {
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(response, status));
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



