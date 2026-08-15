package com.codingshuttle.projects.airBnbApp.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
    private UserDto user;
    private Set<BookingDto> bookings;
}
