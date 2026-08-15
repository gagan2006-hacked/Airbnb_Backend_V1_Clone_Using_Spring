package com.codingshuttle.projects.airBnbApp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HolidayDto {

    private String date;
    private String observed;

    @JsonProperty("public")
    private boolean isPublic;
}