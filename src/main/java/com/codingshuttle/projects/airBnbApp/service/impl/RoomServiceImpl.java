package com.codingshuttle.projects.airBnbApp.service.impl;

import com.codingshuttle.projects.airBnbApp.Exception.except.ResourceNotFoundException;
import com.codingshuttle.projects.airBnbApp.Exception.except.UnAuthorisedException;
import com.codingshuttle.projects.airBnbApp.dto.RoomDto;
import com.codingshuttle.projects.airBnbApp.dto.RoomUpdateDto;
import com.codingshuttle.projects.airBnbApp.entity.Hotel;
import com.codingshuttle.projects.airBnbApp.entity.Room;
import com.codingshuttle.projects.airBnbApp.repository.HotelRepository;
import com.codingshuttle.projects.airBnbApp.repository.RoomRepository;
import com.codingshuttle.projects.airBnbApp.service.Interface.InventoryService;
import com.codingshuttle.projects.airBnbApp.service.Interface.RoomService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper mapper;

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting Room by Id:{}",roomId);
        Room room=roomRepository.findById(roomId).orElseThrow(()->{
            log.error("Room with Id:{} Not Found",roomId);
            return ResourceNotFoundException.builder()
                    .field("Id")
                    .message(getResourceMsg("id",roomId))
                    .build();
        });
        RoomDto dto=mapper.map(room,RoomDto.class);
        log.info("SuccessFully Fetched Room with Id:{}",roomId);
        return dto;
    }

    @Override
    @Transactional
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("Checking the Hotel with Id:{}",hotelId);
        Hotel hotel=hotelRepository.findById(hotelId).orElseThrow(()->{
            log.error("Hotel not Found With Id:{}",hotelId);
            return ResourceNotFoundException.builder()
                    .field("id")
                    .message(HotelServiceImpl.getResourceMsg("id",hotelId))
                    .build();
        });

        if (!hotel.getOwner().equals(UserServiceImpl.getCurrentUser())){
            throw new UnAuthorisedException("User Do Not own the Hotel");
        }

        Room room=mapper.map(roomDto,Room.class);
        room.setHotel(hotel);
        room=roomRepository.save(room);
        log.info("Room Created with Id:{}",room.getId());
        if (hotel.getActive()){
            inventoryService.makeInventoryforRoom(room);
        }
        return mapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomOfHotelWithHotelId(Long hotelId) {
        log.info("Checking the Hotel with Id:{}",hotelId);
        Hotel hotel=hotelRepository.findById(hotelId).orElseThrow(()->{
            log.error("Hotel not Found With Id:{}",hotelId);
            return ResourceNotFoundException.builder()
                    .field("id")
                    .message(HotelServiceImpl.getResourceMsg("id",hotelId))
                    .build();
        });
        return roomRepository.findByHotel(hotel).stream().map((element) -> mapper.map(element, RoomDto.class)).toList();
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Checking Room exist with Id:{}",roomId);
        if (!roomRepository.existsById(roomId)){
            throw  ResourceNotFoundException.builder()
                    .field("Id")
                    .message(getResourceMsg("id",roomId))
                    .build();
        }
        log.info("Getting Room by Id:{}",roomId);
        Room room=roomRepository.findById(roomId).orElseThrow(()->{
            log.error("Room with Id:{} Not Found",roomId);
            return ResourceNotFoundException.builder()
                    .field("Id")
                    .message(getResourceMsg("id",roomId))
                    .build();
        });

        if (!room.getHotel().getOwner().equals(UserServiceImpl.getCurrentUser())){
            throw new UnAuthorisedException("User Do Not own the Hotel");
        }

        inventoryService.deleteAllIventoryByRoom(room);
        roomRepository.deleteById(roomId);
    }

    @Override
    public List<Room> getAllRoomOfHotelByHotelId(Long hotelId) {
        log.info("Checking the Hotel with Id:{}",hotelId);
        Hotel hotel=hotelRepository.findById(hotelId).orElseThrow(()->{
            log.error("Hotel not Found With Id:{}",hotelId);
            return ResourceNotFoundException.builder()
                    .field("id")
                    .message(HotelServiceImpl.getResourceMsg("id",hotelId))
                    .build();
        });
        return roomRepository.findByHotel(hotel);
    }

    @Override
    public RoomDto updateRoomByHotel(Long hotelId, Long roomId,RoomUpdateDto roomUpdateDto) {
        Room room=roomRepository.findById(roomId).orElseThrow(()->{
            log.error("Room with Id:{} Not Found:",roomId);
            return ResourceNotFoundException.builder()
                    .field("Id")
                    .message(getResourceMsg("id",roomId))
                    .build();
        });

        if (!room.getHotel().getOwner().equals(UserServiceImpl.getCurrentUser())){
            throw new UnAuthorisedException("User Do Not own the Hotel");
        }

        mapper.map(roomUpdateDto,room);

        room.setId(roomId);
        roomRepository.save(room);
        return mapper.map(room, RoomDto.class);
    }

    public static String getResourceMsg(String field,Object object){
        return "Room with the "+field+": "+object.toString()+" NOT FOUND";
    }
}
