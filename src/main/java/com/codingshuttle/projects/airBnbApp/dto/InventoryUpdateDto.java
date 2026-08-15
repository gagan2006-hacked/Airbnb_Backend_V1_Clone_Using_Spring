package com.codingshuttle.projects.airBnbApp.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryUpdateDto {

//    add
    @NotNull(message = "Surge Factor can not be Null")
    @DecimalMin(value = "1.0",inclusive = false,message = "Surge Factor should be greater than 1.0 ")
    @Digits(integer = 5,fraction = 2,message = "Surge Factor can have at most 5 digits before decimal and 2 after decimal")
    private BigDecimal surgeFactor;

    @NotNull(message = "Base Price can not be Null")
    @DecimalMin(value = "0.0",inclusive = false,message = "Base Price should be greater than 0.0 ")
    @Digits(integer = 10,fraction = 2,message = "Base Price can have at most 10 digits before decimal and 2 after decimal")
    private BigDecimal price;

    @NotNull(message = "Closed can not be Null")
    private Boolean closed;

}
