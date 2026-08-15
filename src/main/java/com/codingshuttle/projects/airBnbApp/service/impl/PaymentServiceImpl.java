package com.codingshuttle.projects.airBnbApp.service.impl;

import com.codingshuttle.projects.airBnbApp.Exception.except.InvalidPaymentDetails;
import com.codingshuttle.projects.airBnbApp.Exception.except.PaymentGateWayException;
import com.codingshuttle.projects.airBnbApp.Exception.except.ResourceNotFoundException;
import com.codingshuttle.projects.airBnbApp.Exception.except.UnAuthorisedException;
import com.codingshuttle.projects.airBnbApp.dto.BookingInfoDto;
import com.codingshuttle.projects.airBnbApp.dto.GuestDto;
import com.codingshuttle.projects.airBnbApp.dto.OrderInfoDto;
import com.codingshuttle.projects.airBnbApp.dto.PaymentVerificationDto;
import com.codingshuttle.projects.airBnbApp.entity.Booking;
import com.codingshuttle.projects.airBnbApp.entity.Hotel;
import com.codingshuttle.projects.airBnbApp.entity.Payment;
import com.codingshuttle.projects.airBnbApp.entity.enums.BookingStatus;
import com.codingshuttle.projects.airBnbApp.entity.enums.PaymentProvider;
import com.codingshuttle.projects.airBnbApp.entity.enums.PaymentStatus;
import com.codingshuttle.projects.airBnbApp.repository.BookingRepository;
import com.codingshuttle.projects.airBnbApp.repository.InventoryRepository;
import com.codingshuttle.projects.airBnbApp.repository.PaymentRepository;
import com.codingshuttle.projects.airBnbApp.repository.RefundRepository;
import com.codingshuttle.projects.airBnbApp.service.Interface.PaymentService;
import com.codingshuttle.projects.airBnbApp.util.RazorpayProperties;
import com.razorpay.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final InventoryRepository inventoryRepository;
    private final RefundRepository refundRepository;
    private final BookingRepository bookingRepository;

    private final PaymentRepository paymentRepository;

    private final RazorpayClient razorpayClient;

    private final RazorpayProperties razorpayProperties;

    private final ModelMapper mapper;

    @Value("razorpay.webhook.secret")
    private String webhookSecret;

    @Override
    public OrderInfoDto createOrder(Booking booking) throws RazorpayException {
        JSONObject orderRequest = getJsonObject(booking);
        Order order=razorpayClient.orders.create(orderRequest);

        Integer amount = order.get("amount");

        Payment payment=Payment.builder()
                .razorpayOrderId(order.get("id"))
                .paymentStatus(PaymentStatus.CREATED)
                .amount(booking.getAmount())
                .provider(PaymentProvider.RAZORPAY)
                .currency(order.get("currency"))
                .booking(booking)
                .build();

        payment.setAmount(payment.getAmount().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP));
        paymentRepository.save(payment);

        return OrderInfoDto.builder()
                .orderId(payment.getRazorpayOrderId())
                .key(razorpayProperties.getKey())
                .currency(payment.getCurrency())
                .amount(booking.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                .build();
    }

    @Override
    @Transactional
    public BookingInfoDto verifyPayment(PaymentVerificationDto paymentVerificationDto)  {

        Payment payment=paymentRepository.findByRazorpayOrderId(paymentVerificationDto.getRazorpayOrderId())
                .orElseThrow(()-> ResourceNotFoundException.builder()
                        .field("RazorpayOrderId")
                        .message("Payment with this order id does not exist")
                        .build());

        Booking booking = payment.getBooking();
        if (!booking.getUser().equals(UserServiceImpl.getCurrentUser())){
            throw new UnAuthorisedException("User do not own the Booking");
        }

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            return getBookingInfoDto(booking);
        }


        if (booking.getBookingStatus() != BookingStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("Booking is not awaiting payment");
        }

        JSONObject attributes= new JSONObject();

        attributes.put("razorpay_order_id", paymentVerificationDto.getRazorpayOrderId());
        attributes.put("razorpay_payment_id", paymentVerificationDto.getRazorpayPaymentId());
        attributes.put("razorpay_signature", paymentVerificationDto.getRazorpaySignature());

        boolean isValid= false;
        try {
            isValid = Utils.verifyPaymentSignature(attributes,razorpayProperties.getSecret());
        }
        catch (RazorpayException e) {
            log.error("Un expected Error ",e);
            throw new PaymentGateWayException(HttpStatus.INTERNAL_SERVER_ERROR,"Invalid payment signature");
        }

        if (!isValid){
            throw new InvalidPaymentDetails("Invalid Payment Details");
        }

        verificationProcess(paymentVerificationDto);

        return getBookingInfoDto(booking);
    }





    @Override
    @Transactional
    public void initRefund(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> ResourceNotFoundException.builder()
                        .field("bookingId")
                        .message("Booking not found")
                        .build());

        if (!booking.getUser().equals(UserServiceImpl.getCurrentUser())) {
            throw new UnAuthorisedException("User does not own this booking");
        }

        LocalDate now=LocalDate.now();
        if (!now.isBefore(booking.getCheckInDate())){
            throw new InvalidPaymentDetails("Refund is being initiated after the Check In Date");
        }
        if (booking.getBookingStatus()==BookingStatus.CONFIRMED){
            Payment payment=booking.getPayment();
            String paymentId=payment.getRazorpayPaymentId();
            if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
                throw new IllegalStateException("Only successful payments can be refunded");
            }
            try {
//                Full refund
                JSONObject refundRequest=new JSONObject();

                refundRequest.put("amount",booking.getAmount().multiply(BigDecimal.valueOf(100)));
                refundRequest.put("speed","optimum");

                Refund refund=razorpayClient.payments.refund(payment.getRazorpayPaymentId(),refundRequest);

                com.codingshuttle.projects.airBnbApp.entity.Refund refund1= com.codingshuttle.projects
                        .airBnbApp.entity.Refund.builder()
                        .payment(payment).razorpayRefundId(refund.get("id"))
                        .amount(booking.getAmount())
                        .build();

                String status = refund.get("status");

                if (status.equals("processed")){
                    refund1.setStatus(status);
                    payment.setPaymentStatus(PaymentStatus.REFUNDED);
                    paymentRepository.save(payment);

                    booking.setBookingStatus(BookingStatus.CANCELLED);
                } else if ("pending".equals(status)) {
                    refund1.setStatus(status);
                }
                refundRepository.save(refund1);
                return;
            } catch (RazorpayException e) {
                log.error(e.getMessage());
                throw PaymentGateWayException.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build();
            }
        }
        throw new IllegalStateException("Booking is in Invalid State");
    }

    @Override
    @Transactional
    public void verifyPaymentViaWebhook(String payload, String signature) {
        boolean isValid = false;
        try {
            isValid = Utils.verifyWebhookSignature(
                    payload,
                    signature,
                    webhookSecret
            );
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }
        if (!isValid) return;

        JSONObject json = new JSONObject(payload);

        String event = json.getString("event");

        log.info("payload :{}",payload);

        if (event.contains("payment.captured")){
            JSONObject payment = json
                    .getJSONObject("payload")
                    .getJSONObject("payment")
                    .getJSONObject("entity");

            PaymentVerificationDto dto=PaymentVerificationDto.builder()
                    .razorpayOrderId(json.getString("order_id"))
                    .razorpayPaymentId("id")
                    .build();

            verificationProcess(dto);
        }
        else if (event.contains("payment.failed")){
            JSONObject payment = json
                    .getJSONObject("payload")
                    .getJSONObject("payment")
                    .getJSONObject("entity");

            PaymentVerificationDto dto=PaymentVerificationDto.builder()
                    .razorpayOrderId(json.getString("order_id"))
                    .razorpayPaymentId("id")
                    .build();

            paymentFailedHandler(dto);
        }
        else if (event.contains("refund.processed")){
            JSONObject refund = json
                    .getJSONObject("payload")
                    .getJSONObject("refund")
                    .getJSONObject("entity");

            String refundId = refund.getString("id");

            String paymentId = refund.getString("payment_id");

            Integer amount = refund.getInt("amount");

            String currency = refund.getString("currency");

            String status = refund.getString("status");

            refundViaWebhook(refundId,paymentId,amount,currency,status);
        }
        else if (event.contains("refund.failed")){
            JSONObject refund = json
                    .getJSONObject("payload")
                    .getJSONObject("refund")
                    .getJSONObject("entity");

            String refundId = refund.getString("id");

            String paymentId = refund.getString("payment_id");

            Integer amount = refund.getInt("amount");

            String currency = refund.getString("currency");

            String status = refund.getString("status");

            refundViaWebhook(refundId,paymentId,amount,currency,status);
        }
    }

    private void refundViaWebhook(String refundId, String paymentId, Integer amount, String currency, String status) {
        Payment payment=paymentRepository.findByRazorpayPaymentId(paymentId).orElseThrow();
        Booking booking=payment.getBooking();
        if ("processed".equals(status)){
            com.codingshuttle.projects.airBnbApp.entity.Refund refund=refundRepository.findByRazorpayRefundId(refundId).orElseThrow();
            refund.setStatus(status);
            payment.setPaymentStatus(PaymentStatus.REFUNDED);
            booking.setBookingStatus(BookingStatus.CANCELLED);

            inventoryRepository.findAndLockReservedInventory
                    (booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());
            inventoryRepository.cancelBooking
                    (booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());
        }else if ("failed".equals(status)){
            com.codingshuttle.projects.airBnbApp.entity.Refund refund=refundRepository.findByRazorpayRefundId(refundId).orElseThrow();
            refund.setStatus(status);
        }
    }

    @Override
    public void paymentFailedHandler(PaymentVerificationDto dto) {
        Payment payment=paymentRepository.findByRazorpayOrderId(dto.getRazorpayOrderId()).orElse(null);
        if (payment==null)return;

        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.getBooking().setBookingStatus(BookingStatus.EXPIRED);

        Booking booking=payment.getBooking();

        inventoryRepository.findAndLockReservedInventory
                (booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());
        inventoryRepository.cancelReserved
                (booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());
    }

    private JSONObject getJsonObject(Booking booking) {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", booking.getAmount().multiply(BigDecimal.valueOf(100)));
        orderRequest.put("currency",razorpayProperties.getCurrency());
        orderRequest.put("receipt","booking_"+ booking.getId());

        JSONObject notes = new JSONObject();
        notes.put("bookingId", booking.getId());

        Hotel hotel= booking.getHotel();

        notes.put("hotelName",hotel.getName());
        notes.put("hotelCity",hotel.getCity());
        notes.put("room", booking.getRoom().getType());

        orderRequest.put("notes",notes);
        return orderRequest;
    }

    private BookingInfoDto getBookingInfoDto(Booking booking){
        return BookingInfoDto.builder()
                .bookingId(booking.getId())
                .hotelId(booking.getHotel().getId())
                .roomId(booking.getRoom().getId())
                .roomsCount(booking.getRoomsCount())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .guests(booking.getGuests().stream().map((element) -> mapper.map(element, GuestDto.class)).collect(Collectors.toSet()))
                .bookingStatus(booking.getBookingStatus())
                .build();
    }


    private void verificationProcess(PaymentVerificationDto paymentVerificationDto){

        Payment payment=paymentRepository.findByRazorpayOrderId(paymentVerificationDto.getRazorpayOrderId())
                .orElseThrow(()-> ResourceNotFoundException.builder()
                        .field("RazorpayOrderId")
                        .message("Payment with this order id does not exist")
                        .build());

        Booking booking = payment.getBooking();

        if (booking.getBookingStatus()==BookingStatus.EXPIRED){
            return;
        }


        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            return;
        }


        if (booking.getBookingStatus() != BookingStatus.PAYMENT_PENDING) {
            return;
        }



        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setRazorpayPaymentId(
                paymentVerificationDto.getRazorpayPaymentId());
        payment.setRazorpaySignature(
                paymentVerificationDto.getRazorpaySignature());

        booking.setBookingStatus(BookingStatus.CONFIRMED);

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());

        inventoryRepository.confirmBooking(booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());
    }
}



// Orders : {"amount":1000,"amount_paid":0,"notes":{"hotelCity":"Goa","hotelName":"Taj Hotel","bookingId":2,"room":"Deluxe"},"created_at":1784216515,"amount_due":1000,"currency":"INR","receipt":"booking_2","id":"order_TEE9WcPn3evxuY","entity":"order","offer_id":null,"attempts":0,"status":"created"}