package com.example.tire_change_services.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "tireChangeTimesResponse")
public class AvailableTimesResponseXml {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "availableTime")
    List<AvailableTimeXml> availableTimes;

    public List<AvailableTimeXml> getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(List<AvailableTimeXml> availableTimes) {
        this.availableTimes = availableTimes;
    }
}
