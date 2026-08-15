package com.codingshuttle.projects.airBnbApp.controller;

import com.codingshuttle.projects.airBnbApp.dto.ManagerProfileDto;
import com.codingshuttle.projects.airBnbApp.dto.ProfileDto;
import com.codingshuttle.projects.airBnbApp.dto.ProfileUpdateRequest;
import com.codingshuttle.projects.airBnbApp.dto.UserDto;
import com.codingshuttle.projects.airBnbApp.service.Interface.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    @GetMapping("/u")
    public ResponseEntity<ProfileDto> getProfile(){
        return ResponseEntity.ok(userService.getProfile());
    }

    @PreAuthorize("hasRole('HOTEL_MANAGER')")
    @GetMapping("/a")
    public ResponseEntity<ManagerProfileDto> getProfileForHotelAdmin(){
        return ResponseEntity.ok(userService.getProfileForHotelAdmin());
    }

    @PatchMapping("/update")
    public ResponseEntity<UserDto> updateProfile(@RequestBody ProfileUpdateRequest updateRequest){
        return ResponseEntity.ok(userService.updateProfile(updateRequest));
    }
}
