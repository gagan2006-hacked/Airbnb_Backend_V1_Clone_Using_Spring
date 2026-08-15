package com.codingshuttle.projects.airBnbApp.service.Interface;

import com.codingshuttle.projects.airBnbApp.dto.BookingInfoDto;
import com.codingshuttle.projects.airBnbApp.dto.OrderInfoDto;
import com.codingshuttle.projects.airBnbApp.dto.PaymentVerificationDto;
import com.codingshuttle.projects.airBnbApp.entity.Booking;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;

public interface PaymentService {
    OrderInfoDto createOrder(Booking booking) throws RazorpayException;

    BookingInfoDto verifyPayment(@Valid PaymentVerificationDto paymentVerificationDto) throws RazorpayException;

    void initRefund(Long bookingId);

    void verifyPaymentViaWebhook(String payload, String signature);

     void paymentFailedHandler(PaymentVerificationDto dto);
}
