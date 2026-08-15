package com.codingshuttle.projects.airBnbApp.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class HolidaysDTO {
    private HolidayDto[] holidays;
}
