package com.codingshuttle.projects.airBnbApp.service.impl;

import com.codingshuttle.projects.airBnbApp.dto.HotelDto;
import com.codingshuttle.projects.airBnbApp.dto.HotelMinTransferDto;
import com.codingshuttle.projects.airBnbApp.dto.HotelRequestInfo;
import com.codingshuttle.projects.airBnbApp.dto.HotelSearchInfoDto;
import com.codingshuttle.projects.airBnbApp.repository.HotelMinPriceRepository;
import com.codingshuttle.projects.airBnbApp.service.Interface.HotelSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelSearchServiceImpl implements HotelSearchService {
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final ModelMapper modelMapper;



    @Override
    @Cacheable(cacheNames = "hotelSearchCache",
            key = "#hotelRequestInfo.city + ':' + "+"#hotelRequestInfo.startDate + ':' + "+"#hotelRequestInfo.endDate + ':' + "+"#hotelRequestInfo.roomCount")
    public Page<HotelSearchInfoDto> getHotelByStartAndEndDate(HotelRequestInfo hotelRequestInfo) {
        log.info("Searching hotels for {} city, from {} to {}", hotelRequestInfo.getCity(), hotelRequestInfo.getStartDate(), hotelRequestInfo.getEndDate());
        Pageable pageable= PageRequest.of(hotelRequestInfo.getPageNo(),hotelRequestInfo.getPageSize());
        long dateCount= ChronoUnit.DAYS.between(hotelRequestInfo.getStartDate(),hotelRequestInfo.getEndDate())+1;
        Page<HotelMinTransferDto>page=hotelMinPriceRepository.findHotelsWithAvailableInventory(hotelRequestInfo.getCity(),hotelRequestInfo.getStartDate(),hotelRequestInfo.getEndDate(),pageable);
        return page.map(hotel -> HotelSearchInfoDto.builder()
                .hotel(modelMapper.map(hotel.getHotel(),HotelDto.class))
                .price(hotel.getPrice())
                .build());
    }
}
