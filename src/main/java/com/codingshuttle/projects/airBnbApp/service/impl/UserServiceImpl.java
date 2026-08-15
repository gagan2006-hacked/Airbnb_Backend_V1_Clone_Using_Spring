package com.codingshuttle.projects.airBnbApp.service.impl;

import com.codingshuttle.projects.airBnbApp.Exception.except.ResourceNotFoundException;
import com.codingshuttle.projects.airBnbApp.dto.*;
import com.codingshuttle.projects.airBnbApp.entity.Booking;
import com.codingshuttle.projects.airBnbApp.entity.Hotel;
import com.codingshuttle.projects.airBnbApp.entity.User;
import com.codingshuttle.projects.airBnbApp.repository.BookingRepository;
import com.codingshuttle.projects.airBnbApp.repository.UserRepository;
import com.codingshuttle.projects.airBnbApp.service.Interface.HotelService;
import com.codingshuttle.projects.airBnbApp.service.Interface.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final HotelService hotelService;

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()-> ResourceNotFoundException.builder()
                .field("Email")
                .message("No User Present with this Email :{ "+email+" }")
                .build());
    }

    @Override
    public ProfileDto getProfile() {
        Long id=getCurrentUser().getId();

        User user=userRepository.findById(id).orElseThrow(()-> ResourceNotFoundException.builder()
                    .field("Id")
                    .message("No User Present with this Id :{ "+id+" }")
                    .build());

        return ProfileDto.builder()
                        .user(modelMapper.map(user, UserDto.class))
                        .bookings(user.getBookings().stream().map((element) -> modelMapper.map(element, BookingDto.class)).collect(Collectors.toSet()))
                        .build();
    }

    @Override
    @Transactional
    public ManagerProfileDto getProfileForHotelAdmin() {
        Long id=getCurrentUser().getId();

        User user=userRepository.findById(id).orElseThrow(()-> ResourceNotFoundException.builder()
                .field("Id")
                .message("No User Present with this Id :{ "+id+" }")
                .build());

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));

        Set<Booking>bookings=new HashSet<>();

        Set<Hotel>hotels=user.getHotels();

        hotels.forEach((element)->{
            bookings.addAll(bookingRepository.findAllByHotel(element,pageable).toList());
        });



        return ManagerProfileDto.builder()
                .user(modelMapper.map(user, UserDto.class))
                .hotels(hotels.stream().map((element) -> modelMapper.map(element, HotelDto.class)).collect(Collectors.toSet()))
                .bookings(bookings.stream().map((element) -> modelMapper.map(element, BookingDto.class)).collect(Collectors.toSet()))
                .hotelReport(hotelService.getReport(id))
                .build();
    }

    @Override
    public UserDto updateProfile(ProfileUpdateRequest updateRequest) {
        Long id=getCurrentUser().getId();

        User user=userRepository.findById(id).orElseThrow(()-> ResourceNotFoundException.builder()
                .field("Id")
                .message("No User Present with this Id :{ "+id+" }")
                .build());

        user.setName(updateRequest.getName());
        user.setGender(updateRequest.getGender());
        user.setDateOfBirth(updateRequest.getDateOfBirth());
        user=userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return getUserByEmail(username);
    }


    public static User getCurrentUser(){
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user;
    }
}
