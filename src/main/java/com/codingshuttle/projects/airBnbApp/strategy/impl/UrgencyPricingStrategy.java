package com.codingshuttle.projects.airBnbApp.strategy.impl;

import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import com.codingshuttle.projects.airBnbApp.strategy.PricingStrategy;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy {

    private final PricingStrategy savedValue;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal saved=savedValue.calculatePrice(inventory);
        if (inventory.getDate().isAfter(LocalDate.now().plusDays(7))){
            return saved;
        }
        return saved.multiply(BigDecimal.valueOf(1.5));
    }

}
