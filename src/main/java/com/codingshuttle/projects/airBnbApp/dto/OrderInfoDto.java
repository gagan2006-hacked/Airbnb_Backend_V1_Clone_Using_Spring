package com.codingshuttle.projects.airBnbApp.dto;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoDto {
    private String key;
    private String orderId;
    private String currency;
    private Long amount;
}
