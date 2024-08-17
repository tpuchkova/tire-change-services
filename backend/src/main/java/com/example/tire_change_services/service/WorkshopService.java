package com.example.tire_change_services.service;

import com.example.tire_change_services.model.AvailableTime;
import com.example.tire_change_services.model.NameValue;
import com.example.tire_change_services.model.WorkshopsAndCarTypes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkshopService {
    final List<IWorkshop> workshops;

    public List<AvailableTime> getAvailableTimes(String from, String until, String workshopName, String carType) {
        List<AvailableTime> availableTimes = new ArrayList<>();
        for (IWorkshop workshop : workshops) {
            if (StringUtils.hasText(workshopName)) {
                if (workshop.getWorkshopInfo().getName().equals(workshopName)) {
                    if (StringUtils.hasText(carType)){
                        if (workshop.getWorkshopInfo().getCarTypes().contains(carType)){
                            availableTimes.addAll(workshop.getAvailableTimes(from, until, workshopName, carType));
                        }
                    } else {
                        availableTimes.addAll(workshop.getAvailableTimes(from, until, workshopName, carType));
                    }
                }
            } else {
                if (StringUtils.hasText(carType)){
                    if (workshop.getWorkshopInfo().getCarTypes().contains(carType)) {
                        availableTimes.addAll(workshop.getAvailableTimes(from, until, workshopName, carType));
                    }
                } else {
                    availableTimes.addAll(workshop.getAvailableTimes(from, until, workshopName, carType));
                }
            }
        }

        ZonedDateTime fromDateTime = ZonedDateTime.parse(from + "T00:00:00Z");
        ZonedDateTime untilDateTime = ZonedDateTime.parse(until + "T23:59:59Z");
        List<AvailableTime> sortedFilteredTimes = availableTimes.stream()
                .filter(at -> {
                    ZonedDateTime time = ZonedDateTime.parse(at.getTime());
                    return !time.isBefore(fromDateTime) && !time.isAfter(untilDateTime);
                })
                .sorted(Comparator.comparing(at -> ZonedDateTime.parse(at.getTime())))
                .collect(Collectors.toList());


        return sortedFilteredTimes;
    }

  public AvailableTime bookTime(String id, String workshopName, String contactInformation) {
        IWorkshop workshop = findWorkshop(workshopName);
        return workshop.bookTime(id, contactInformation);
    }

    private IWorkshop findWorkshop(String workshopName) {
        return workshops.stream()
                .filter(ws -> workshopName.equals(ws.getWorkshopInfo().getName()))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public WorkshopsAndCarTypes getWorkshopsAndCarTypes() {
        Set<NameValue> workshopNames = new HashSet<>();
        Set<NameValue> carTypes = new HashSet<>();
        for (IWorkshop workshop : workshops) {
            workshopNames.add(new NameValue(StringUtils.capitalize(workshop.getWorkshopInfo().getName()), workshop.getWorkshopInfo().getName()));

            String[] parts = workshop.getWorkshopInfo().getCarTypes().split(",");
            for (String part : parts) {
                carTypes.add(new NameValue(StringUtils.capitalize(part), part));
            }
        }

        return new WorkshopsAndCarTypes(workshopNames, carTypes);
    }
}
