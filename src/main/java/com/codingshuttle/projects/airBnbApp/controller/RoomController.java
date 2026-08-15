package com.codingshuttle.projects.airBnbApp.controller;

import com.codingshuttle.projects.airBnbApp.dto.RoomDto;
import com.codingshuttle.projects.airBnbApp.dto.RoomUpdateDto;
import com.codingshuttle.projects.airBnbApp.service.Interface.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService service;

    @GetMapping(path = "/{roomId}")
    public ResponseEntity<RoomDto> getRoomById (@PathVariable Long roomId){
        return new ResponseEntity<>(service.getRoomById(roomId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RoomDto> createNewRoom(@PathVariable Long hotelId,
                                                 @RequestBody @Valid RoomDto roomDto) {
        RoomDto room = service.createNewRoom(hotelId, roomDto);
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRoomOfHotelWithHotelId(@PathVariable Long hotelId){
        return new ResponseEntity<>(service.getAllRoomOfHotelWithHotelId(hotelId),HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<RoomDto> deleteRoomById(@PathVariable Long hotelId, @PathVariable Long roomId) {
        service.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{roomId}")
    public ResponseEntity<RoomDto>  updateFullRoomOfHotel(@PathVariable Long hotelId, @PathVariable Long roomId,@RequestBody @Valid RoomUpdateDto roomUpdateDto){
        return new ResponseEntity<>(service.updateRoomByHotel(hotelId, roomId,roomUpdateDto), HttpStatus.OK);
    }
}
