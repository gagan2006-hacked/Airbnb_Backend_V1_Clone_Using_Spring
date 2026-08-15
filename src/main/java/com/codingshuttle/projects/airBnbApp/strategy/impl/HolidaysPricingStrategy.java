package com.codingshuttle.projects.airBnbApp.strategy.impl;

import com.codingshuttle.projects.airBnbApp.dto.HolidaysDTO;
import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import com.codingshuttle.projects.airBnbApp.strategy.PricingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

@RequiredArgsConstructor
public class HolidaysPricingStrategy implements PricingStrategy {

    private final PricingStrategy savedValue;
    private final RestClient api;

    @Value("${holiday.api.key}")
    private String holidayApiUrl;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal savedPrice=savedValue.calculatePrice(inventory);
        HolidaysDTO holidayDtos=api.get()
                .uri(holidayApiUrl, LocalDate.now().getYear()-1)
                .retrieve().body(HolidaysDTO.class);

        if (holidayDtos!=null){
        boolean isHoliday = Arrays.stream(holidayDtos.getHolidays())
                .anyMatch(h -> h.getDate().equals(inventory.getDate().toString()));
        if (isHoliday) return savedPrice.multiply(BigDecimal.valueOf(1.30));
        }
        return savedPrice;

    }
}
