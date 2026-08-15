package com.codingshuttle.projects.airBnbApp.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {

    private Long id;

    @NotNull(message = "Room type can not be Null")
    @NotBlank(message = "Room type can not be Blank")
    @Size(min = 3,max = 30,message = "Room Type can be from 3 to 30 character")
    private String type;

    @NotNull(message = "Base Price can not be Null")
    @DecimalMin(value = "0.0",inclusive = false,message = "Base Price should be greater than 0.0 ")
    @Digits(integer = 10,fraction = 2,message = "Base Price can have at most 10 digits before decimal and 2 after decimal")
    private BigDecimal basePrice;

    private String[] photos;
    private String[] amenities;

    @Min(value = 1,message = "Minimum 1 room should be Present in the Hotel")
    private Integer totalCount;

    @NotNull(message = "Capacity of the room should not be null")
    @Min(value = 1,message = "Room capacity should be minimum 1")
    private Integer capacity;

}
