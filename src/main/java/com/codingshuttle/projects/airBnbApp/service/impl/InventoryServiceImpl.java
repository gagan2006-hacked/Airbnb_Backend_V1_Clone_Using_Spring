package com.codingshuttle.projects.airBnbApp.service.impl;

import com.codingshuttle.projects.airBnbApp.Exception.except.ResourceNotFoundException;
import com.codingshuttle.projects.airBnbApp.Exception.except.UnAuthorisedException;
import com.codingshuttle.projects.airBnbApp.dto.InventoryDto;
import com.codingshuttle.projects.airBnbApp.dto.InventoryUpdateDto;
import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import com.codingshuttle.projects.airBnbApp.entity.Room;
import com.codingshuttle.projects.airBnbApp.repository.InventoryRepository;
import com.codingshuttle.projects.airBnbApp.repository.RoomRepository;
import com.codingshuttle.projects.airBnbApp.service.Interface.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.codingshuttle.projects.airBnbApp.service.impl.RoomServiceImpl.getResourceMsg;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    private final ModelMapper modelMapper;
    private final RoomRepository roomRepository;

    private final InventoryRepository inventoryRepository;

    private final PricingService pricingService;



    @Override
    public void makeInventoryforRoom(Room room) {
        LocalDate today=LocalDate.now();
        LocalDate oneYear=today.plusYears(1);
        for (LocalDate date=today;!date.isAfter(oneYear);date=date.plusDays(1)){
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .date(date)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);
        }

        pricingService.updatePricePerHotel(room.getHotel());
    }

    @Override
    public void deleteAllIventoryByRoom(Room room) {
        inventoryRepository.deleteAllByRoom(room);
    }

    @Override
    @Transactional
    public Page<InventoryDto> getAllInventoriesOfARoom(Long roomId, Integer pageNo, Integer pageSize, LocalDate startDate, LocalDate endDate) {


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

        Pageable pageable= PageRequest.of(pageNo,pageSize);

        Page<Inventory>inventories=inventoryRepository.findByRoomAndDateBetween(room,startDate,endDate,pageable);

        return inventories.map((element) -> modelMapper.map(element, InventoryDto.class));
    }

    @Override
    @Transactional
    public Page<InventoryDto> updateBatchInventoriesByRoom(Long roomId, Integer pageNo, Integer pageSize, InventoryUpdateDto updateDto, LocalDate startDate, LocalDate endDate) {

        if (startDate.isBefore(LocalDate.now()) || endDate.isBefore(LocalDate.now())) {
            throw new IllegalStateException("Updation is not allowed for inventories of Past Date");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalStateException("Updation is not allowed for Because End Date should be after Start Date");
        }

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

        List<Inventory>inventories=inventoryRepository.findByRoomAndDateBetween(room,startDate,endDate);

        inventoryRepository.updateInventoryByData(updateDto.getPrice(),updateDto.getSurgeFactor(),updateDto.getClosed(),room.getId(),startDate,endDate);

        Pageable pageable=PageRequest.of(pageNo,pageSize);

        Page<Inventory>page=inventoryRepository.findByRoomAndDateBetween(room,startDate,endDate,pageable);
        return page.map((element) -> modelMapper.map(element, InventoryDto.class));
    }

// TODO Add Logs in Every Services
}
