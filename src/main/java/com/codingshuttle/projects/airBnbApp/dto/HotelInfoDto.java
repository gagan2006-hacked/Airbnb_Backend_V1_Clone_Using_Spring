package com.codingshuttle.projects.airBnbApp.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class HotelInfoDto {
    private HotelDto hotel;
    private Set<RoomDto>rooms;
}
