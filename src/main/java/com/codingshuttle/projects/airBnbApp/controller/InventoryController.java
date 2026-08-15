package com.codingshuttle.projects.airBnbApp.controller;

import com.codingshuttle.projects.airBnbApp.dto.InventoryDto;
import com.codingshuttle.projects.airBnbApp.dto.InventoryUpdateDto;
import com.codingshuttle.projects.airBnbApp.service.Interface.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping(path = "/room/{roomId}")
    public ResponseEntity<Page<InventoryDto>> getAllInventoriesOfARoom(@PathVariable Long roomId,
                                                                       @RequestParam(required = false,defaultValue = "0") Integer pageNo,
                                                                       @RequestParam(required = false,defaultValue = "40") Integer pageSize,
                                                                       @RequestParam(required = false) LocalDate startDate,
                                                                       @RequestParam(required = false) LocalDate endDate)
    {

        if (startDate==null)startDate=LocalDate.now();
        if (endDate==null)endDate=startDate.plusDays(50);

        return ResponseEntity.ok(inventoryService.getAllInventoriesOfARoom(roomId,pageNo,pageSize,startDate,endDate));
    }

    @PatchMapping("/room/{roomId}")
    public ResponseEntity<Page<InventoryDto>> updateBatchInventoriesByRoom(@PathVariable Long roomId,
                                                                           @RequestParam(required = false,defaultValue = "0") Integer pageNo,
                                                                           @RequestParam(required = false,defaultValue = "40") Integer pageSize,
                                                                           @RequestBody @Valid InventoryUpdateDto updateDto,
                                                                           @RequestParam LocalDate startDate,
                                                                           @RequestParam LocalDate endDate)

    {
        return ResponseEntity.ok(inventoryService.updateBatchInventoriesByRoom(roomId,pageNo,pageSize,updateDto,startDate,endDate));
    }

}
