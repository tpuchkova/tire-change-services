package com.example.tire_change_services.service;

import com.example.tire_change_services.config.WorkshopInfo;

import com.example.tire_change_services.config.WorkshopsInfoList;
import com.example.tire_change_services.exception.WorkshopCommunicationException;
import com.example.tire_change_services.model.AvailableTime;
import com.example.tire_change_services.model.AvailableTimeXml;
import com.example.tire_change_services.model.AvailableTimesResponseXml;
import com.example.tire_change_services.model.WorkshopError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
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
    public WorkshopInfo getWorkshopInfo() {
        return workshops.getWorkshops().get("london");
    }

    @Override
    public List<AvailableTime> getAvailableTimes(String from, String until, String workshopName, String carType) {
        String formattedUntilDate = add1DayToDate(until);
        String url = String.format("%s/api/v1/tire-change-times/available?from=%s&until=%s", workshops.getWorkshops().get("london").getUrl(), from, formattedUntilDate);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String xml = response.getBody();
            XmlMapper xmlMapper = new XmlMapper();
            List<AvailableTime> availableTimes = new ArrayList<>();

            AvailableTimesResponseXml availableTimesResponseXml = getAvailableTimesResponseXml(xmlMapper, xml);
            if (availableTimesResponseXml.getAvailableTimes() != null) {
                for (AvailableTimeXml availableTimeXml : availableTimesResponseXml.getAvailableTimes()) {
                    availableTimes.add(createAvailableTime(availableTimeXml));
                }
            }

            return availableTimes;
        } catch (HttpStatusCodeException e) {
            throwException(e);
        }
        return List.of();
    }

    @Override
    public AvailableTime bookTime(String id, String contactInformation) {
        String url = String.format("%s/api/v1/tire-change-times/%s/booking", workshops.getWorkshops().get("london").getUrl(), id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<String>(PUT_REQUEST_TEMPLATE.formatted(contactInformation), headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
            String xml = response.getBody();
            XmlMapper xmlMapper = new XmlMapper();
            AvailableTimeXml availableTimeXml = getAvailableTimeXml(xmlMapper, xml);
            return createAvailableTime(availableTimeXml);
        } catch (HttpStatusCodeException e) {
            throwException(e);
        }
        return null;
    }

    private void throwException(HttpStatusCodeException e) {
            XmlMapper xmlMapper = new XmlMapper();

        WorkshopError error;
        try {
            error = xmlMapper.readValue(e.getResponseBodyAsString(), WorkshopError.class);
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("cannot parse xml response", jsonProcessingException);
            throw new WorkshopCommunicationException(HttpStatus.UNPROCESSABLE_ENTITY, "bad_workshop_response", "Bad workshop response");
        }
        throw new WorkshopCommunicationException(e.getStatusCode(), error.getCode(), error.getMessage());
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

    private AvailableTimeXml getAvailableTimeXml(XmlMapper xmlMapper, String xml) {
        try {
            return xmlMapper.readValue(xml, AvailableTimeXml.class);
        } catch (JsonProcessingException e) {
            throw new WorkshopCommunicationException(HttpStatus.UNPROCESSABLE_ENTITY, "bad_workshop_response", "Bad workshop response");
        }
    }

    private AvailableTimesResponseXml getAvailableTimesResponseXml(XmlMapper xmlMapper, String xml) {
        try {
            return xmlMapper.readValue(xml, AvailableTimesResponseXml.class);
        } catch (JsonProcessingException e) {
            throw new WorkshopCommunicationException(HttpStatus.UNPROCESSABLE_ENTITY, "bad_workshop_response", "Bad workshop response");
        }
    }

    private String add1DayToDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);
        LocalDate newDate = localDate.plusDays(1);
        return newDate.format(formatter);
    }
}
