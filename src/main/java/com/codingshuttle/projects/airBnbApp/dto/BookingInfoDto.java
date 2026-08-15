package com.codingshuttle.projects.airBnbApp.dto;

import com.codingshuttle.projects.airBnbApp.entity.enums.BookingStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingInfoDto {

    private Long bookingId;

    @NotNull(message = "Hotel ID Can't be Null")
    private Long hotelId;

    @NotNull(message = "Room ID Can't be Null")
    private Long roomId;

    @NotNull(message = "Room Count Can't be Null")
    @Min(value = 1,message = "Room Count can not be less than 1")
    private Integer roomsCount;

    @FutureOrPresent(message = "Check In Date Should be Present or Future")
    private LocalDate checkInDate;

    @FutureOrPresent(message = "Check Out Date Should be Present or Future")
    private LocalDate checkOutDate;

    private BookingStatus bookingStatus;
    private Set<GuestDto> guests;
}
