package com.codingshuttle.projects.airBnbApp.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpRequestDto {
    @NotBlank(message = "Name of the user can't be blank")
    @Size(min = 3,max = 150,message = "Name should be greater than 2 and less than 151 ( 3 <= name <= 150 )")
    private String name;

    @NotNull(message = "Email can not be null")
    @Email
    private String email;

    @NotNull(message = "Password can not be null")
    @NotBlank(message = "Password of the user can't be blank")
    @Size(min = 8,max = 36,message = "Password should be greater than 8 and less than 36 ( 8 <= name <= 36 )")
    private String password;
}
