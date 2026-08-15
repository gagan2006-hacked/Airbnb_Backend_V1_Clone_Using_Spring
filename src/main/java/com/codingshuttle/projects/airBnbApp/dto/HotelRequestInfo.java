package com.codingshuttle.projects.airBnbApp.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class HotelRequestInfo {
    private String city;

    @FutureOrPresent(message = "Start Date Should be Present or Future")
    private LocalDate startDate;
    @FutureOrPresent(message = "End Date Should be Present or Future")
    private LocalDate endDate;

    @Min(value = 1,message = "Room Count can not be less than 1")
    @NotNull
    private Integer roomCount;

    @Min(value = 0,message = "Page No can not be less than 0")
    @Max(value = 100)
    @NotNull
    private Integer pageNo=0;

    @Min(value = 1,message = "Page Size can not be less than 1")
    @Max(value = 100)
    @NotNull
    private Integer pageSize=10;
}
