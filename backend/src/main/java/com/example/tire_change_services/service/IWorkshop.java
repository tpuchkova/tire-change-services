package com.example.tire_change_services.service;

import com.example.tire_change_services.config.WorkshopInfo;
import com.example.tire_change_services.config.WorkshopsInfoList;
import com.example.tire_change_services.exception.WorkshopCommunicationException;
import com.example.tire_change_services.model.AvailableTime;

import java.util.List;

public interface IWorkshop {
    WorkshopInfo getWorkshopInfo();
    List<AvailableTime> getAvailableTimes(String from, String until, String workshopName, String carType) throws WorkshopCommunicationException;
    AvailableTime bookTime(String id, String contactInformation);
}
