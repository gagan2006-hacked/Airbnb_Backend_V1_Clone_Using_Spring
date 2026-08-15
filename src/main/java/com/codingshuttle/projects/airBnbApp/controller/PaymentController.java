package com.codingshuttle.projects.airBnbApp.controller;


import com.codingshuttle.projects.airBnbApp.dto.BookingInfoDto;
import com.codingshuttle.projects.airBnbApp.dto.PaymentVerificationDto;
import com.codingshuttle.projects.airBnbApp.service.Interface.PaymentService;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/verify")
    public ResponseEntity<BookingInfoDto> verifyPayment(@RequestBody @Valid PaymentVerificationDto paymentVerificationDto) throws RazorpayException {
        return ResponseEntity.ok(paymentService.verifyPayment(paymentVerificationDto));
    }


    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
        paymentService.verifyPaymentViaWebhook(payload,signature);
        return ResponseEntity.noContent().build();
    }

}
