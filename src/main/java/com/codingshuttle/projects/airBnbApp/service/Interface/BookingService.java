package com.codingshuttle.projects.airBnbApp.service.Interface;

import com.codingshuttle.projects.airBnbApp.dto.BookingDto;
import com.codingshuttle.projects.airBnbApp.dto.BookingInfoDto;
import com.codingshuttle.projects.airBnbApp.dto.BookingRequestDto;
import com.codingshuttle.projects.airBnbApp.dto.OrderInfoDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookingService {
    BookingInfoDto initBooking(@Valid BookingRequestDto bookingRequestDto);

    BookingInfoDto addGuestsToBooking(Long bookingId, @Valid List<Long> guestDtos);

    OrderInfoDto initiatePayment(Long bookingId);

    void cancelBooking(Long bookingId);

    Page<BookingDto> getAllBookingOfHotel(Long hotelId, Integer pageNo, Integer pageSize);
}
