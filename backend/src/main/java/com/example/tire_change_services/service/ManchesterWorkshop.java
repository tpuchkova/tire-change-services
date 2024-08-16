package com.example.tire_change_services.service;

import com.example.tire_change_services.config.WorkshopInfo;
import com.example.tire_change_services.config.WorkshopsInfoList;
import com.example.tire_change_services.exception.WorkshopCommunicationException;
import com.example.tire_change_services.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManchesterWorkshop implements IWorkshop {
    private final WorkshopsInfoList workshops;
    private final RestTemplate restTemplate;

    @Override
    public WorkshopInfo getWorkshopInfo(){
        return workshops.getWorkshops().get("manchester");
    }

    @Override
    public List<AvailableTime> getAvailableTimes(String from, String until, String workshopName, String carType) throws WorkshopCommunicationException {
        String url =  String.format("%s/api/v2/tire-change-times?from=%s", workshops.getWorkshops().get("manchester").getUrl(), from);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String json = response.getBody();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<AvailableTimeJSON>>() {
            }.getType();
            List<AvailableTimeJSON> availableTimesJSON = gson.fromJson(json, listType);
            List<AvailableTime> availableTimes = new ArrayList<>();
            for (AvailableTimeJSON availableTimeJson : availableTimesJSON) {
                if (availableTimeJson.isAvailable()) {
                    availableTimes.add(createAvailableTime(availableTimeJson));
                }
            }

            return availableTimes;
        } catch (HttpStatusCodeException e){
            throwException(e);
        }
        return null;
    }

    @Override
    public AvailableTime bookTime(String id, String contactInformation) {
        ContactInformationRequestBody contactInformationRequestBody = new ContactInformationRequestBody();
        contactInformationRequestBody.setContactInformation(contactInformation);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ContactInformationRequestBody> requestEntity = new HttpEntity<>(contactInformationRequestBody, headers);
        String url =  String.format("%s/api/v2/tire-change-times/%s/booking", workshops.getWorkshops().get("manchester").getUrl(), id);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            String json = response.getBody();
            Gson gson = new Gson();
            Type type = new TypeToken<AvailableTimeJSON>() {
            }.getType();
            AvailableTimeJSON availableTimeJSON = gson.fromJson(json, type);
            return createAvailableTime(availableTimeJSON);
        } catch (HttpStatusCodeException e){
            throwException(e);
        }
        return null;
    }

    private void throwException(HttpStatusCodeException e) {
        Gson gson = new Gson();
        WorkshopError error = gson.fromJson(e.getResponseBodyAsString(), WorkshopError.class);
        throw new WorkshopCommunicationException(e.getStatusCode(), error.getCode(), error.getMessage());
    }

    private AvailableTime createAvailableTime(AvailableTimeJSON availableTimeJson) {
        return AvailableTime.builder()
                .id(String.valueOf(availableTimeJson.getId()))
                .time(availableTimeJson.getTime())
                .workshopName(workshops.getWorkshops().get("manchester").getName())
                .address(workshops.getWorkshops().get("manchester").getAddress())
                .carTypes(workshops.getWorkshops().get("manchester").getCarTypes())
                .build();
    }
}


