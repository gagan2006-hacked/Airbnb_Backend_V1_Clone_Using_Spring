package com.codingshuttle.projects.airBnbApp.strategy.impl;

import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import com.codingshuttle.projects.airBnbApp.strategy.PricingStrategy;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class OccupancyPricingStrategy implements PricingStrategy {

    private final PricingStrategy savedValue;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal savedPrice=savedValue.calculatePrice(inventory);
        double occupancyRate=(double) (inventory.getBookedCount()+inventory.getReservedCount())/ inventory.getTotalCount();
        if (occupancyRate>0.79){
            return savedPrice.multiply(BigDecimal.valueOf(1.5));
        }
        return savedPrice;
    }
}
