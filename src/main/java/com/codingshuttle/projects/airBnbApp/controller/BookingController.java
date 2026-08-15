package com.codingshuttle.projects.airBnbApp.controller;
import com.codingshuttle.projects.airBnbApp.dto.BookingInfoDto;
import com.codingshuttle.projects.airBnbApp.dto.BookingRequestDto;
import com.codingshuttle.projects.airBnbApp.dto.OrderInfoDto;
import com.codingshuttle.projects.airBnbApp.service.Interface.BookingService;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingInfoDto> createBooking(@RequestBody @Valid BookingRequestDto bookingRequestDto){
        return ResponseEntity.status(201).body(bookingService.initBooking(bookingRequestDto));
    }

    @PatchMapping("/{bookingId}/guests")
    public ResponseEntity<BookingInfoDto>  addGuests(@PathVariable Long bookingId, @RequestBody  List<Long> guestDtos){
        return ResponseEntity.ok(bookingService.addGuestsToBooking(bookingId,guestDtos));
    }

    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<OrderInfoDto> initiatePayment(@PathVariable Long bookingId)  {
        return ResponseEntity.ok(bookingService.initiatePayment(bookingId));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId){
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}
