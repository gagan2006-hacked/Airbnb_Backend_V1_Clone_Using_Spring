package com.codingshuttle.projects.airBnbApp.service.Interface;

import com.codingshuttle.projects.airBnbApp.dto.RoomDto;
import com.codingshuttle.projects.airBnbApp.dto.RoomUpdateDto;
import com.codingshuttle.projects.airBnbApp.entity.Room;
import jakarta.validation.Valid;

import java.util.List;

public interface RoomService {
    RoomDto getRoomById(Long roomId);

    RoomDto createNewRoom(Long hotelId, @Valid RoomDto roomDto);

    List<RoomDto> getAllRoomOfHotelWithHotelId(Long hotelId);

    void deleteRoomById(Long roomId);

    List<Room> getAllRoomOfHotelByHotelId(Long hotelId);

    RoomDto updateRoomByHotel(Long hotelId, Long roomId, RoomUpdateDto roomUpdateDto);

}
