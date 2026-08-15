package com.codingshuttle.projects.airBnbApp.strategy.impl;

import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import com.codingshuttle.projects.airBnbApp.strategy.PricingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class PricingStrategyImpl implements PricingStrategy {

    @Qualifier("holidayClient")
    private final RestClient api;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        log.info("Calculating the Pricing of Inventory id:{}",inventory.getId());
        PricingStrategy strategy=new BasePricingStrategy();
        strategy=new SurgePricingStrategy(strategy);
        strategy=new OccupancyPricingStrategy(strategy);
        strategy=new HolidaysPricingStrategy(strategy,api);
        strategy=new UrgencyPricingStrategy(strategy);
        log.info("Returning the Price of the Inventory id:{}",inventory.getId());
        return strategy.calculatePrice(inventory);
    }

}
