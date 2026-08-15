package com.codingshuttle.projects.airBnbApp.dto;

import com.codingshuttle.projects.airBnbApp.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelMinTransferDto {
    private Hotel hotel;
    private BigDecimal price;

    public HotelMinTransferDto(Hotel hotel, Double price) {
        this.hotel = hotel;
        this.price =  price==null ? null : BigDecimal.valueOf(price);
    }
}
