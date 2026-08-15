package com.codingshuttle.projects.airBnbApp.service.impl;

import com.codingshuttle.projects.airBnbApp.Exception.except.BookingExpiredException;
import com.codingshuttle.projects.airBnbApp.Exception.except.PaymentGateWayException;
import com.codingshuttle.projects.airBnbApp.Exception.except.ResourceNotFoundException;
import com.codingshuttle.projects.airBnbApp.Exception.except.UnAuthorisedException;
import com.codingshuttle.projects.airBnbApp.dto.*;
import com.codingshuttle.projects.airBnbApp.entity.*;
import com.codingshuttle.projects.airBnbApp.entity.enums.BookingStatus;
import com.codingshuttle.projects.airBnbApp.entity.enums.PaymentStatus;
import com.codingshuttle.projects.airBnbApp.repository.*;
import com.codingshuttle.projects.airBnbApp.service.Interface.BookingService;
import com.codingshuttle.projects.airBnbApp.service.Interface.PaymentService;
import com.codingshuttle.projects.airBnbApp.util.RazorpayProperties;
import com.razorpay.RazorpayException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final PaymentRepository paymentRepository;
    private final GuestRepository guestRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper mapper;
    private final RoomRepository roomRepository;

    private final HotelRepository hotelRepository;

    private final BookingRepository bookingRepository;

    private final PricingService pricingService;

    private final PaymentService paymentService;

    private final RazorpayProperties razorpayProperties;


    @Override
    @Transactional
    public BookingInfoDto initBooking(BookingRequestDto bookingRequest) {

        log.info("Initialising booking for hotel : {}, room: {}, date {}-{}", bookingRequest.getHotelId(),
                bookingRequest.getRoomId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());

        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId()).
                orElseThrow(() -> ResourceNotFoundException.builder()
                .field("id")
                .message(HotelServiceImpl.getResourceMsg("id",bookingRequest.getHotelId()))
                .build());


        log.info("Getting Room by Id:{}",bookingRequest.getRoomId());
        Long roomId= bookingRequest.getRoomId();
        Room room=roomRepository.findById(roomId).orElseThrow(()->{
            log.error("Room with Id:{} Not Found",roomId);
            return ResourceNotFoundException.builder()
                    .field("Id")
                    .message(RoomServiceImpl.getResourceMsg("id",roomId))
                    .build();
        });

        List<Inventory> inventories=inventoryRepository.findAndLockAvailableInventory(room.getId(),
                bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        long days= ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate())+1;

        if (inventories.size()!=days){
            throw new IllegalStateException("Room is not available anymore");
        }

        inventoryRepository.updateReserved(room.getId(),
                bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        pricingService.updatePricePerDate(hotel,bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate());



        BigDecimal pricePerRoom=pricingService.getTotalPrice(inventories);
        BigDecimal totalAmt=pricePerRoom
                .multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(UserServiceImpl.getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(totalAmt)
                .build();
        booking=bookingRepository.save(booking);

        BookingInfoDto dto=mapper.map(booking,BookingInfoDto.class);
        dto.setBookingId(booking.getId());
        dto.setHotelId(hotel.getId());
        dto.setRoomId(room.getId());
        dto.setCheckInDate(bookingRequest.getCheckInDate());
        dto.setCheckOutDate(bookingRequest.getCheckOutDate());
        return dto;
    }

    @Override
    @Transactional
    public BookingInfoDto addGuestsToBooking(Long bookingId, List<Long> guestDtos) {
        log.info("Getting Booking with ID : {} ",bookingId);
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->ResourceNotFoundException.builder()
                .message(getResourceMsg("ID",bookingId))
                .field("ID")
                .build());

        if (!booking.getUser().equals(UserServiceImpl.getCurrentUser())){
            throw new UnAuthorisedException("User Do not own this booking");
        }

        if (isExpired(booking)){
            throw new BookingExpiredException("Booking Expired, Create a New Booking");
        }

        Set<Guest>guests=new HashSet<>(guestDtos.stream()
                .map(id->guestRepository.findById(id)
                        .orElseThrow(()->ResourceNotFoundException.builder()
                                .field("Id")
                                .message("Guest With ID: "+id+" is Not Found")
                                .build()))
                .toList()
        );
        booking.setGuests(guests);
        booking.setBookingStatus(BookingStatus.GUEST_ADDED);
        return BookingInfoDto.builder()
                .bookingId(bookingId)
                .hotelId(booking.getHotel().getId())
                .roomId(booking.getRoom().getId())
                .roomsCount(booking.getRoomsCount())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .guests(guests.stream().map((element) -> mapper.map(element, GuestDto.class)).collect(Collectors.toSet()))
                .bookingStatus(booking.getBookingStatus())
                .build();
    }

    @Override
    @Transactional
    public OrderInfoDto initiatePayment(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->ResourceNotFoundException.builder()
                .message(getResourceMsg("ID",bookingId))
                .field("ID")
                .build());

        if (!booking.getUser().equals(UserServiceImpl.getCurrentUser())){
            throw new UnAuthorisedException("User Do not own this booking");
        }
        BookingStatus status=booking.getBookingStatus();
        if (status==BookingStatus.PAYMENT_PENDING&&
                booking.getPayment().getPaymentStatus()==PaymentStatus.CREATED){

            Payment payment=booking.getPayment();
            BigDecimal bigDecimal=payment.getAmount().multiply(BigDecimal.valueOf(100));
            return OrderInfoDto.builder()
                    .key(razorpayProperties.getKey())
                    .orderId(payment.getRazorpayOrderId())
                    .currency(payment.getCurrency())
                    .amount(bigDecimal.longValue())
                    .build();
        }


        if (!(status==BookingStatus.RESERVED||status==BookingStatus.GUEST_ADDED)){
            throw new IllegalStateException("Booking is not available for Payment ");
        }

        if (!isExpired(booking)){
            booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
            try {
                return paymentService.createOrder(booking);
            } catch (RazorpayException e) {
                log.error("Failed to create Razorpay order : ",e);
                String message = e.getMessage();

                if (message.contains("Authentication")) {
                    throw  PaymentGateWayException.builder()
                            .message("Payment gateway authentication failed")
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build();
                }

                if (message.contains("BAD_REQUEST_ERROR")) {
                    throw  PaymentGateWayException.builder()
                            .message("Invalid payment request")
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build();
                }

                if (message.contains("Gateway Timeout")
                        || message.contains("timed out")) {
                    throw PaymentGateWayException.builder()
                            .message("Payment gateway timeout")
                            .status(HttpStatus.SERVICE_UNAVAILABLE)
                            .build();
                }

                throw PaymentGateWayException.builder()
                        .message("Unable to create payment order")
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .build();
            }
        }
        else {
            throw new BookingExpiredException("Booking Expired, Create a New Booking");

        }
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->ResourceNotFoundException.builder()
                .message(getResourceMsg("ID",bookingId))
                .field("ID")
                .build());

        if (!booking.getUser().equals(UserServiceImpl.getCurrentUser())){
            throw new UnAuthorisedException("User Do not own this booking");
        }


        if (booking.getBookingStatus()==BookingStatus.CONFIRMED) {
            paymentService.initRefund(bookingId);
            inventoryRepository.findAndLockReservedInventory
                    (booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());

            inventoryRepository.cancelBooking
                    (booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());
        }
        else if (booking.getBookingStatus() == BookingStatus.PAYMENT_PENDING) {
            PaymentVerificationDto dto=PaymentVerificationDto.builder()
                    .razorpayOrderId(booking.getPayment().getRazorpayOrderId())
                    .razorpayPaymentId(booking.getPayment().getRazorpayPaymentId())
                    .build();


            paymentService.paymentFailedHandler(dto);

            booking.setBookingStatus(BookingStatus.CANCELLED);
        } else if (booking.getBookingStatus() == BookingStatus.RESERVED || booking.getBookingStatus()==BookingStatus.GUEST_ADDED) {

            inventoryRepository.findAndLockReservedInventory
                    (booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());
            inventoryRepository.cancelReserved
                    (booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());

            booking.setBookingStatus(BookingStatus.CANCELLED);
        }
    }

    @Override
    @Transactional
    public Page<BookingDto> getAllBookingOfHotel(Long hotelId, Integer pageNo, Integer pageSize) {
        Hotel hotel = hotelRepository.findById(hotelId).
                orElseThrow(() -> ResourceNotFoundException.builder()
                        .field("id")
                        .message(HotelServiceImpl.getResourceMsg("id",hotelId))
                        .build());

        if (!hotel.getOwner().equals(UserServiceImpl.getCurrentUser())){
            throw new UnAuthorisedException("User Do Not own the Hotel");
        }

        Pageable pageable= PageRequest.of(pageNo,pageSize);
        Page<Booking> bookings=bookingRepository.findAllByHotel(hotel,pageable);
        return bookings.map((element) ->{
            BookingDto dto=mapper.map(element, BookingDto.class);
            dto.setHotel(mapper.map(hotel,HotelDto.class));
            dto.setRoom(mapper.map(element.getRoom(), RoomDto.class));
            dto.setGuests(element.getGuests().stream().map((guest) -> mapper.map(guest, GuestDto.class)).collect(Collectors.toSet()));
            dto.setPayment(mapper.map(element.getPayment(), PaymentDto.class));
            return dto;
        });
    }

//    private void mapFullToInfoDto(BookingInfoDto dto, Booking booking) {
//        dto.setBookingId(booking.getId());
//        dto.setHotelId(booking.getHotel().getId());
//        dto.setRoomId(booking.getRoom().getId());
//
//    }

    public  boolean isExpired(Booking booking){
        if (booking.getBookingStatus()==BookingStatus.EXPIRED)return true;
        return !booking.getCreatedAt().plusMinutes(30).isAfter(LocalDateTime.now());
    }

    public static String getResourceMsg(String field,Object object){
        return "Booking with the "+field+": "+object.toString()+" NOT FOUND";
    }

    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void updateBookingStatusForGuestAdded(){
        int pageNo=0;
        int pageSize=200;
        Page<Booking>page=bookingRepository.findExpiredBookings
                (BookingStatus.GUEST_ADDED,LocalDateTime.now().minusMinutes(30),PageRequest.of(pageNo,pageSize));
        while (!page.isEmpty()){
            page.getContent().forEach(booking -> {
                booking.setBookingStatus(BookingStatus.EXPIRED);

                inventoryRepository.findAndLockReservedInventory
                        (booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());
                inventoryRepository.cancelReserved(booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());
                bookingRepository.save(booking);
            });
        }

    }

    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void updateBookingStatusForPaymentPending(){
        int pageNo=0;
        int pageSize=200;
        Page<Booking>page=bookingRepository.findExpiredBookings
                (BookingStatus.PAYMENT_PENDING,LocalDateTime.now().minusMinutes(30),PageRequest.of(pageNo,pageSize));
        while (!page.isEmpty()){
            page.getContent().forEach(booking -> {
                booking.setBookingStatus(BookingStatus.EXPIRED);

                Payment payment=booking.getPayment();

                payment.setPaymentStatus(PaymentStatus.EXPIRED);

                inventoryRepository.findAndLockReservedInventory
                        (booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());
                inventoryRepository.cancelReserved(booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());
                paymentRepository.save(payment);
                bookingRepository.save(booking);
            });
        }

    }



}
