package com.codingshuttle.projects.airBnbApp.Exception.except;

import lombok.*;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PaymentGateWayException extends RuntimeException {
    private HttpStatus status;
    private String message;
    public PaymentGateWayException(String message) {
        super(message);
        this.message=message;
    }

}
