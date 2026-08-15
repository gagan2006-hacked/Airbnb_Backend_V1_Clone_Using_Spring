package com.codingshuttle.projects.airBnbApp.dto;

import com.codingshuttle.projects.airBnbApp.entity.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuestDto {

    @NotBlank(message = "Name can not be blank")
    @Size(min = 3,max = 150,message = "Name should be greater than 2 and less than 151 ( 3 <= name <= 150 )")
    private String name;

    @NotNull(message = "Gender Can't be Null")
    private Gender gender;

    @Min(value = 18,message = "Minimum Age is 18")
    @Max(value = 150,message = "Maximum Age is 150")
    @NotNull(message = "Age Can't be Null")
    private Integer age;

}
