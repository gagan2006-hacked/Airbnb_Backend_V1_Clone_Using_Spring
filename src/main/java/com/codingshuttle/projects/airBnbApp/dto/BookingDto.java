package com.codingshuttle.projects.airBnbApp.dto;

import com.codingshuttle.projects.airBnbApp.entity.enums.BookingStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Long bookingId;

    @NotNull(message = "Hotel Can't be Null")
    private HotelDto hotel;

    @NotNull(message = "Room Can't be Null")
    private RoomDto room;

    @NotNull(message = "Room Count Can't be Null")
    @Min(value = 1,message = "Room Count can not be less than 1")
    private Integer roomsCount;

    @FutureOrPresent(message = "Check In Date Should be Present or Future")
    private LocalDate checkInDate;

    @FutureOrPresent(message = "Check Out Date Should be Present or Future")
    private LocalDate checkOutDate;

    private BookingStatus bookingStatus;
    private Set<GuestDto> guests;

    @NotNull(message = "Amount can not be Null")
    @DecimalMin(value = "0.0",inclusive = false,message = "Amount should be greater than 0.0 ")
    @Digits(integer = 10,fraction = 2,message = "Amount can have at most 10 digits before")
    private BigDecimal amount;

    private PaymentDto payment;

    private LocalDateTime createdAt;
}
