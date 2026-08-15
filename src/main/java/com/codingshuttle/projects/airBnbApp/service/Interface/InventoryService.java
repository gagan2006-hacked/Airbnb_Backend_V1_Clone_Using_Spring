package com.codingshuttle.projects.airBnbApp.service.Interface;

import com.codingshuttle.projects.airBnbApp.dto.InventoryDto;
import com.codingshuttle.projects.airBnbApp.dto.InventoryUpdateDto;
import com.codingshuttle.projects.airBnbApp.entity.Room;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface InventoryService {
    void makeInventoryforRoom(Room room);

    void deleteAllIventoryByRoom(Room room);

    Page<InventoryDto> getAllInventoriesOfARoom(Long roomId, Integer pageNo, Integer pageSize, LocalDate startDate, LocalDate endDate);

    Page<InventoryDto> updateBatchInventoriesByRoom(Long roomId, Integer pageNo, Integer pageSize, @Valid InventoryUpdateDto updateDto, LocalDate startDate, LocalDate endDate);
}
