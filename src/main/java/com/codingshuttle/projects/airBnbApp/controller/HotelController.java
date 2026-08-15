package com.codingshuttle.projects.airBnbApp.controller;

import com.codingshuttle.projects.airBnbApp.dto.*;
import com.codingshuttle.projects.airBnbApp.service.Interface.BookingService;
import com.codingshuttle.projects.airBnbApp.service.Interface.HotelService;
import com.codingshuttle.projects.airBnbApp.service.Interface.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/hotels")
public class HotelController {

    private final HotelService service;
    private final BookingService bookingService;
    private final RoomService roomService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long id){
        return ResponseEntity.ok(service.getHotelById(id));
    }

    @PostMapping
    public ResponseEntity<HotelDto> createHotel(@RequestBody @Valid HotelDto dto){
        return new ResponseEntity<>(service.createHotel(dto), HttpStatusCode.valueOf(201));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<HotelDto> updateHotel(@RequestBody @Valid HotelDto dto,@PathVariable Long id){
        return new ResponseEntity<>(service.updateHotel(dto,id), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable Long id){
        service.deleteHotelById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(path = "/{id}/active")
    public ResponseEntity<Void> activeHotelById(@PathVariable Long id){
        service.activeHotelById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<HotelDto>> getAllHotelsOfUser(@RequestParam(required = false,defaultValue = "0") Integer pageNo,
                                                             @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        return ResponseEntity.ok(service.getHotelByUser(pageSize,pageNo));
    }

    @GetMapping(path = "/{hotelId}/bookings")
    public ResponseEntity<Page<BookingDto>> getAllBookingOfHotel(@PathVariable Long hotelId,
                                                                 @RequestParam(required = false,defaultValue = "0") Integer pageNo,
                                                                 @RequestParam(required = false,defaultValue = "20") Integer pageSize){
        return ResponseEntity.ok(bookingService.getAllBookingOfHotel(hotelId,pageNo,pageSize));
    }

    @GetMapping(path = "/{hotelId}/report")
    public ResponseEntity<HotelReportDto> getHotelReport(@PathVariable Long hotelId){
        return ResponseEntity.ok(service.getReport(hotelId));
    }


}
