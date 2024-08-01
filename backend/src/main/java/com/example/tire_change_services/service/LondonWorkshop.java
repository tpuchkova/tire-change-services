package com.example.tire_change_services.service;

import com.example.tire_change_services.config.WorkshopInfo;

import com.example.tire_change_services.config.WorkshopsInfoList;
import com.example.tire_change_services.model.AvailableTime;
import com.example.tire_change_services.model.AvailableTimeXml;
import com.example.tire_change_services.model.AvailableTimesResponseXml;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LondonWorkshop implements IWorkshop {
    private final WorkshopsInfoList workshops;
    private final RestTemplate restTemplate;

    private static final String PUT_REQUEST_TEMPLATE = """
            <?xml version="1.0" encoding="UTF-8"?>
            <london.tireChangeBookingRequest>
            	<contactInformation>%s</contactInformation>
            </london.tireChangeBookingRequest>
            """;

    @Override
    public WorkshopInfo getWorkshopInfo(){
        return workshops.getWorkshops().get("london");
    }

    @Override
    public List<AvailableTime> getAvailableTimes(String from, String until, String workshopName, String carType) {
        String url =  String.format("%s/api/v1/tire-change-times/available?from=%s&until=%s", workshops.getWorkshops().get("london").getUrl(), from, until); // Replace with your target URL
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String xml = response.getBody();
        XmlMapper xmlMapper = new XmlMapper();
        List<AvailableTime> availableTimes = new ArrayList<>();
        try {
            AvailableTimesResponseXml availableTimesResponseXml = xmlMapper.readValue(xml, AvailableTimesResponseXml.class);
            for (AvailableTimeXml availableTimeXml : availableTimesResponseXml.getAvailableTimes()) {
                availableTimes.add(createAvailableTime(availableTimeXml));
            }

        } catch (Exception ex){
            //return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            throw new RuntimeException("Failed to parse XML response", ex);
        }
        return availableTimes;
    }

    @Override
    public AvailableTime bookTime(String id, String contactInformation) {
        String url =  String.format("%s/api/v1/tire-change-times/%s/booking", workshops.getWorkshops().get("london").getUrl(), id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<String>(PUT_REQUEST_TEMPLATE.formatted(contactInformation), headers);

        HttpEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        String xml = response.getBody();
        XmlMapper xmlMapper = new XmlMapper();
        AvailableTimeXml availableTimeXml;
        try {
            availableTimeXml = xmlMapper.readValue(xml, AvailableTimeXml.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return createAvailableTime(availableTimeXml);
    }

    private AvailableTime createAvailableTime(AvailableTimeXml availableTimeXml) {
        return AvailableTime.builder()
                .id(availableTimeXml.getId())
                .time(availableTimeXml.getTime())
                .workshopName(workshops.getWorkshops().get("london").getName())
                .address(workshops.getWorkshops().get("london").getAddress())
                .carTypes(workshops.getWorkshops().get("london").getCarTypes())
                .build();
    }
}
