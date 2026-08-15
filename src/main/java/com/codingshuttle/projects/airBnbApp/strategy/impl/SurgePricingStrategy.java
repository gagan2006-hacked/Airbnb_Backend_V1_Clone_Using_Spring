package com.codingshuttle.projects.airBnbApp.strategy.impl;

import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import com.codingshuttle.projects.airBnbApp.strategy.PricingStrategy;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class SurgePricingStrategy implements PricingStrategy {

    private final PricingStrategy savedValue;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return savedValue.calculatePrice(inventory).multiply(inventory.getSurgeFactor());
    }

}
