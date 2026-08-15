package com.codingshuttle.projects.airBnbApp.service.Interface;

import com.codingshuttle.projects.airBnbApp.dto.HotelDto;
import com.codingshuttle.projects.airBnbApp.dto.HotelRequestInfo;
import com.codingshuttle.projects.airBnbApp.dto.HotelSearchInfoDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface HotelSearchService {
    Page<HotelSearchInfoDto> getHotelByStartAndEndDate(@Valid HotelRequestInfo hotelRequestInfo);
}
