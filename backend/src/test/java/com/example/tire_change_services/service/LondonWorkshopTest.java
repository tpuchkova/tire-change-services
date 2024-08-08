package com.example.tire_change_services.service;

import com.example.tire_change_services.config.WorkshopInfo;
import com.example.tire_change_services.config.WorkshopsInfoList;
import com.example.tire_change_services.model.AvailableTime;
import com.example.tire_change_services.model.AvailableTimeXml;
import com.example.tire_change_services.model.AvailableTimesResponseXml;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class LondonWorkshopTest {
    private static final String CORRECT_RESPONSE_XML =
            """
                <tireChangeTimesResponse>
                    <availableTime>
                        <uuid>68f2cacb-a591-4e6c-96af-2d4854372ce6</uuid>
                        <time>2024-07-11T11:00:00Z</time>
                    </availableTime>
                </tireChangeTimesResponse>
            """;
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WorkshopsInfoList workshopsInfoList;

    @InjectMocks
    private LondonWorkshop londonWorkshop;

//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        when(workshopInfo.getUrl()).thenReturn("http://test-url.com");
//    }

    @Test
    void shouldSuccessfullyParseResponse() {
        // given
        mockRestTemplateGetForEntity(CORRECT_RESPONSE_XML, HttpStatus.OK);

        when(workshopsInfoList.getWorkshops()).thenReturn(Maps.newHashMap("london",
                WorkshopInfo.builder()
                        .url("correctUrl")
                        .name("london")
                        .address("address")
                        .carTypes("passenger car")
                .build()));

        // when
        List<AvailableTime> availableTimes = londonWorkshop.getAvailableTimes("2024-07-26", "2024-07-27", "london", "passenger car");

        // then

        assertNotNull(availableTimes);
        assertEquals("68f2cacb-a591-4e6c-96af-2d4854372ce6", availableTimes.get(0).getId());
        assertEquals("2024-07-11T11:00:00Z", availableTimes.get(0).getTime());
        assertEquals("london", availableTimes.get(0).getWorkshopName());
        assertEquals("address", availableTimes.get(0).getAddress());
        assertEquals("passenger car", availableTimes.get(0).getCarTypes());
    }

    @Test
    public void testGetAvailableTimes_Success() throws Exception {
        String xmlResponse = "<AvailableTimesResponseXml>...</AvailableTimesResponseXml>";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(xmlResponse, HttpStatus.OK));

        XmlMapper xmlMapper = new XmlMapper();
        AvailableTimesResponseXml availableTimesResponseXml = xmlMapper.readValue(xmlResponse, AvailableTimesResponseXml.class);
        when(xmlMapper.readValue(anyString(), eq(AvailableTimesResponseXml.class)))
                .thenReturn(availableTimesResponseXml);

        List<AvailableTime> availableTimes = londonWorkshop.getAvailableTimes("2024-07-26", "2024-07-27", "london", "passenger car");

        assertNotNull(availableTimes);
      //  assertEquals(availableTimes.get(0).getWorkshopName(), "london");
    }

    @Test
    public void testGetAvailableTimes_Error() {
        // Mock an error response from the REST call
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Error"));

        // Call the method and expect an exception
        assertThrows(RuntimeException.class, () -> {
            londonWorkshop.getAvailableTimes("2024-07-26", "2024-07-27", "Workshop1", "CarType1");
        });
    }

    @Test
    public void testBookTime_Success() throws JsonProcessingException {
        // Define test inputs
        String id = "123";
        String contactInformation = "contact@example.com";

        // Mock the REST response
        String xmlResponse = "<AvailableTimeXml>...</AvailableTimeXml>";
        ResponseEntity<String> mockResponse = new ResponseEntity<>(xmlResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        // Mock the parsing result
        XmlMapper xmlMapper = new XmlMapper();
        AvailableTimeXml availableTimeXml = new AvailableTimeXml(); // Populate with test data as needed
        when(xmlMapper.readValue(xmlResponse, AvailableTimeXml.class)).thenReturn(availableTimeXml);

        // Call the method under test
        AvailableTime result = londonWorkshop.bookTime(id, contactInformation);

        // Assertions
        assertNotNull(result);
        // Additional assertions to verify the result
    }

    private void mockRestTemplateGetForEntity(String response, HttpStatus status) {
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(response, status));
    }
}

