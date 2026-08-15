package com.codingshuttle.projects.airBnbApp.dto;

import com.codingshuttle.projects.airBnbApp.entity.HotelContactInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelDto {
    private Long id;
    @NotBlank(message = "Name of the Hotel cannot be blank")
    @Size(min = 3,max = 150,message = "Hotel Name Can be contain character of 3 to 150")
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;

    @NotNull(message = "Active should be true or false")
    private Boolean active;
}
