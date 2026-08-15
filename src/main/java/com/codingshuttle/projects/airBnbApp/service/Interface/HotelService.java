package com.codingshuttle.projects.airBnbApp.service.Interface;

import com.codingshuttle.projects.airBnbApp.dto.HotelDto;
import com.codingshuttle.projects.airBnbApp.dto.HotelInfoDto;
import com.codingshuttle.projects.airBnbApp.dto.HotelReportDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface HotelService {
    HotelDto getHotelById(Long id);

    HotelDto createHotel(@Valid HotelDto dto);

    HotelDto updateHotel(@Valid HotelDto dto, Long id);

    void deleteHotelById(Long id);

    void activeHotelById(Long id);

    HotelInfoDto getHotelByIdAndInfo(Long hotelId);

    Page<HotelDto> getHotelByUser(Integer pageSize, Integer pageNo);

    HotelReportDto getReport(Long hotelId);
}
