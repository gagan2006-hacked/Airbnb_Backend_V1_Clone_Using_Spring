package com.codingshuttle.projects.airBnbApp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerificationDto {
    @NotBlank(message = "Order Id Can not be blank")
    private String razorpayOrderId;

    @NotBlank(message = "Payment Id Can not be blank")
    private String razorpayPaymentId;

    @NotBlank(message = "Pay Signature Can not be blank")
    private String razorpaySignature;
}
