package com.codingshuttle.projects.airBnbApp.dto;

import jakarta.validation.Valid;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelReportDto {

    @Valid
    private HotelDto hotel;

    @Valid
    private Set<RoomDto> rooms;

    private Integer bookingCount;

    private BigDecimal totalRevenue;

    private BigDecimal avgRevenuePerBooking;
}
