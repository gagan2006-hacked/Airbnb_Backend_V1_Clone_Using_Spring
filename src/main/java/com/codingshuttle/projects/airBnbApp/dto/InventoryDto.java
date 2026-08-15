package com.codingshuttle.projects.airBnbApp.dto;

import jakarta.persistence.Column;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class InventoryDto {

    private Long id;

    private LocalDate date;


    private Integer bookedCount;

    private Integer totalCount;

    private Integer reservedCount;

    private BigDecimal surgeFactor;

    private BigDecimal price;

    private String city;

    private Boolean closed;

    private LocalDateTime createdAt;
}
