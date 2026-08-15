package com.codingshuttle.projects.airBnbApp.service.impl;

import com.codingshuttle.projects.airBnbApp.Exception.except.ResourceNotFoundException;
import com.codingshuttle.projects.airBnbApp.Exception.except.UnAuthorisedException;
import com.codingshuttle.projects.airBnbApp.dto.*;
import com.codingshuttle.projects.airBnbApp.entity.Booking;
import com.codingshuttle.projects.airBnbApp.entity.Hotel;
import com.codingshuttle.projects.airBnbApp.entity.Room;
import com.codingshuttle.projects.airBnbApp.entity.User;
import com.codingshuttle.projects.airBnbApp.repository.BookingRepository;
import com.codingshuttle.projects.airBnbApp.repository.HotelRepository;
import com.codingshuttle.projects.airBnbApp.service.Interface.HotelService;
import com.codingshuttle.projects.airBnbApp.service.Interface.InventoryService;
import com.codingshuttle.projects.airBnbApp.service.Interface.RoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {
    private final BookingRepository bookingRepository;

    private final HotelRepository hotelRepository;
    private final ModelMapper mapper;
    private final RoomService roomService;
    private final InventoryService inventoryService;

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting Hotel By Id :{}",id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.builder()
                .field("id")
                .message(getResourceMsg("id",id))
                .build());
        log.info("Returning hotel With Id:{}",id);
        return mapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto createHotel(HotelDto dto) {
        log.info("Creating Hotel With Name:{}",dto.getName());

        Hotel hotel=mapper.map(dto,Hotel.class);
        hotel.setActive(false);
        hotel.setOwner(UserServiceImpl.getCurrentUser());
        hotel=hotelRepository.save(hotel);
        log.info("Created Hotel with name:{} and id:{}",hotel.getName(),hotel.getId());
        return mapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotel(HotelDto dto, Long id) {
        log.info("Update Hotel with Id:{}",id);
        Hotel hotel=hotelRepository.findById(id).orElseThrow(()->{
            log.error("Hotel not Found With Id:{}",id);
            return ResourceNotFoundException.builder()
                    .field("id")
                    .message(getResourceMsg("id",id))
                    .build();
        });

        if (!hotel.getOwner().equals(UserServiceImpl.getCurrentUser())){
            throw new UnAuthorisedException("User Do Not own the Hotel");
        }

        mapper.map(dto,hotel);
        HotelDto dto1=mapper.map(hotelRepository.save(hotel), HotelDto.class);
        log.info("Hotel updated successfully with Id:{}",id);
        return dto1;
    }

    @Override
    public void deleteHotelById(Long id) {
        log.info("Deleting Hotel with Id:{}",id);
        if (hotelRepository.existsById(id)){
            Hotel hotel=hotelRepository.findById(id).orElse(null);

            if (hotel!= null &&!hotel.getOwner().equals(UserServiceImpl.getCurrentUser())){
                throw new UnAuthorisedException("User Do Not own the Hotel");
            }
            hotelRepository.deleteById(id);
            log.info("Hotel with Id:{} is deleted",id);
            return;
        }
        log.error("Hotel With Id:{} not found",id);
        throw ResourceNotFoundException.builder()
                .field("id")
                .message(getResourceMsg("id",id))
                .build();
    }

    @Override
    @Transactional
    public void activeHotelById(Long id) {
        log.info("Activating Hotel with Id:{}",id);
        Hotel hotel=hotelRepository.findById(id).orElseThrow(()->{
            log.error("Hotel not Found With Id:{}",id);
            return ResourceNotFoundException.builder()
                    .field("id")
                    .message(getResourceMsg("id",id))
                    .build();
        });

        if (!hotel.getOwner().equals(UserServiceImpl.getCurrentUser())){
            throw new UnAuthorisedException("User Do Not own the Hotel");
        }

        if (!hotel.getActive()) {
            hotel.setActive(true);
            for (Room room :roomService.getAllRoomOfHotelByHotelId(id)){
                inventoryService.makeInventoryforRoom(room);
            }
        }
    }

    @Override
    public HotelInfoDto getHotelByIdAndInfo(Long id) {
        log.info("Getting Hotel By Id :{} in INFO METHOD",id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.builder()
                .field("id")
                .message(getResourceMsg("id",id))
                .build());
        Set<Room>rooms=hotel.getRooms();
        return HotelInfoDto.builder()
                .hotel(mapper.map(hotel,HotelDto.class))
                .rooms(new HashSet<>(rooms.stream()
                        .map((element) -> mapper.map(element, RoomDto.class)).toList()))
                .build();
    }

    @Override
    public Page<HotelDto> getHotelByUser(Integer pageSize, Integer pageNo) {
        User user=UserServiceImpl.getCurrentUser();
        Pageable pageable= PageRequest.of(pageNo,pageSize);
        Page<Hotel>hotels=hotelRepository.findByOwner(user,pageable);
        return hotels.map((element) -> mapper.map(element, HotelDto.class));
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRED)
    public HotelReportDto getReport(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).
                orElseThrow(() -> ResourceNotFoundException.builder()
                        .field("id")
                        .message(HotelServiceImpl.getResourceMsg("id",hotelId))
                        .build());

        if (!hotel.getOwner().equals(UserServiceImpl.getCurrentUser())){
            throw new UnAuthorisedException("User Do Not own the Hotel");
        }

        List<Booking> bookings=bookingRepository.findAllByHotel(hotel);

        int bookingCount=bookings.size();

        BigDecimal totalRevenue=bookings.stream()
                .map(Booking::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);


        BigDecimal avgRevenuePerBooking=(bookingCount>0)?
                totalRevenue.divide(BigDecimal.valueOf(bookingCount), RoundingMode.HALF_UP):BigDecimal.ZERO;


        return HotelReportDto.builder()
                .hotel(mapper.map(hotel, HotelDto.class))
                .rooms(hotel.getRooms().stream().map((element) -> mapper.map(element, RoomDto.class)).collect(Collectors.toSet()))
                .bookingCount(bookingCount)
                .totalRevenue(totalRevenue)
                .avgRevenuePerBooking(avgRevenuePerBooking)
                .build();
    }


    public static String getResourceMsg(String field,Object object){
        return "Hotel with the "+field+": "+object.toString()+" NOT FOUND";
    }


}
