package com.codingshuttle.projects.airBnbApp.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelSearchInfoDto {
    private HotelDto hotel;
    private BigDecimal price;
}
