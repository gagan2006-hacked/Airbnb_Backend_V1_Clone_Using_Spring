package com.codingshuttle.projects.airBnbApp.service.Interface;

import com.codingshuttle.projects.airBnbApp.dto.ManagerProfileDto;
import com.codingshuttle.projects.airBnbApp.dto.ProfileDto;
import com.codingshuttle.projects.airBnbApp.dto.ProfileUpdateRequest;
import com.codingshuttle.projects.airBnbApp.dto.UserDto;
import com.codingshuttle.projects.airBnbApp.entity.User;

public interface UserService {
    User getUserByEmail(String email);

    ProfileDto getProfile();

    ManagerProfileDto getProfileForHotelAdmin();

    UserDto updateProfile(ProfileUpdateRequest updateRequest);
}
