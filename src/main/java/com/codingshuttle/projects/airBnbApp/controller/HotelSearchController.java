package com.codingshuttle.projects.airBnbApp.controller;

import com.codingshuttle.projects.airBnbApp.dto.HotelInfoDto;
import com.codingshuttle.projects.airBnbApp.dto.HotelRequestInfo;
import com.codingshuttle.projects.airBnbApp.dto.HotelSearchInfoDto;
import com.codingshuttle.projects.airBnbApp.service.Interface.HotelSearchService;
import com.codingshuttle.projects.airBnbApp.service.Interface.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/hotels/search")
public class HotelSearchController {

    private final HotelSearchService hotelSearchService;
    private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<Page<HotelSearchInfoDto>> getHotelByStartAndEndDate(@RequestBody @Valid HotelRequestInfo hotelRequestInfo){
        return ResponseEntity.ok(hotelSearchService.getHotelByStartAndEndDate(hotelRequestInfo));
    }

    @GetMapping(path = "/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelByIDAndStartAndEndDate(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelByIdAndInfo(hotelId));
    }
}
