package com.codingshuttle.projects.airBnbApp.service.impl;

import com.codingshuttle.projects.airBnbApp.entity.Hotel;
import com.codingshuttle.projects.airBnbApp.entity.HotelMinPrice;
import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import com.codingshuttle.projects.airBnbApp.repository.HotelMinPriceRepository;
import com.codingshuttle.projects.airBnbApp.repository.HotelRepository;
import com.codingshuttle.projects.airBnbApp.repository.InventoryRepository;
import com.codingshuttle.projects.airBnbApp.strategy.PricingStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PricingService{
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final HotelRepository hotelRepository;

    private final PricingStrategy strategy;

    @Scheduled(cron = "0 */15 * * * *")

    public void updatePrice(){
        int pageNo=0;
        int pageSize=100;
        Page<Hotel>page=hotelRepository.findAll(PageRequest.of(pageNo,pageSize));
        while (!page.isEmpty()){
            page.getContent().forEach(this::updatePricePerHotel);
            page=hotelRepository.findAll(PageRequest.of(++pageNo,pageSize));
        }
    }

    public void updatePricePerHotel(Hotel hotel) {
        log.info("Updating hotel prices for hotel ID: {}", hotel.getId());

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory>list=inventoryRepository.findByHotelAndDateBetween(hotel,startDate,endDate);
        updateInventaryPrice(list);

        updateHotelMinPrice(hotel,list,startDate,endDate);
    }

    public void updatePricePerDate(Hotel hotel,LocalDate startDate,LocalDate endDate ) {
        log.info("Updating hotel prices for Hotel ID: {}", hotel.getId());

        List<Inventory>list=inventoryRepository.findByHotelAndDateBetween(hotel,startDate,endDate);
        updateInventaryPrice(list);

        updateHotelMinPrice(hotel,list,startDate,endDate);
    }



    public void updateHotelMinPrice(Hotel hotel, List<Inventory> list, LocalDate startDate, LocalDate endDate) {
        HashMap<LocalDate,BigDecimal>map=new HashMap<>();
        for (Inventory i:list){
            if (map.containsKey(i.getDate())){
                map.put(i.getDate(),map.get(i.getDate()).min(i.getPrice()));
            }else {
                map.put(i.getDate(),i.getPrice());
            }
        }

        List<HotelMinPrice> hotelPrices = new ArrayList<>();
        map.forEach((date,price)->{
            HotelMinPrice hotelMinPrice=hotelMinPriceRepository.findByHotelAndDate(hotel,date).orElse(new HotelMinPrice(hotel,date));
            hotelMinPrice.setPrice(price);
            hotelPrices.add(hotelMinPrice);
        });
        hotelMinPriceRepository.saveAll(hotelPrices);
    }

    public void updateInventaryPrice(List<Inventory> list) {
        list.forEach(inventory -> {
            BigDecimal newPrice=strategy.calculatePrice(inventory);
            inventory.setPrice(newPrice);
        });
        inventoryRepository.saveAll(list);
    }

    public BigDecimal getTotalPrice(List<Inventory> inventories) {
        return inventories.stream()
                .map(Inventory::getPrice)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }
}
