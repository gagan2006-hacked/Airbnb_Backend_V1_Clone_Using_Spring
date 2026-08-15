package com.codingshuttle.projects.airBnbApp.dto;

import jakarta.validation.Valid;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManagerProfileDto {
    @Valid
    private UserDto user;

    @Valid
    private Set<HotelDto> hotels;

    @Valid
    private Set<BookingDto> bookings;

    @Valid
    private HotelReportDto hotelReport;
}
