package com.example.tire_change_services.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="AvailableTime")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AvailableTimeXml {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JacksonXmlProperty(localName = "uuid")
    private String id;
    @JacksonXmlProperty(localName = "time")
    private String time;
}

