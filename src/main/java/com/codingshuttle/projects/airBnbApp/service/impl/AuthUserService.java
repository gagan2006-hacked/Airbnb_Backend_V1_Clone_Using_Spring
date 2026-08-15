package com.codingshuttle.projects.airBnbApp.service.impl;

import com.codingshuttle.projects.airBnbApp.Exception.except.DuplicateEntryException;
import com.codingshuttle.projects.airBnbApp.dto.LoginRequestDto;
import com.codingshuttle.projects.airBnbApp.dto.SignUpRequestDto;
import com.codingshuttle.projects.airBnbApp.dto.UserDto;
import com.codingshuttle.projects.airBnbApp.entity.User;
import com.codingshuttle.projects.airBnbApp.entity.enums.Role;
import com.codingshuttle.projects.airBnbApp.repository.UserRepository;
import com.codingshuttle.projects.airBnbApp.service.Interface.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthUserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder encoder;
    private final AuthenticationManager manager;
    private final JwtService jwtService;
    private final UserService userService;


    public UserDto signUp(@Valid SignUpRequestDto signUpRequest) {
      if (userRepository.existsByEmail(signUpRequest.getEmail())){
          throw DuplicateEntryException.builder()
                  .field("Email")
                  .message("User with The Email Already Exists")
                  .build();
      }
      User newUser=mapper.map(signUpRequest, User.class);
      newUser.setRoles(Set.of(Role.GUEST));
      newUser.setPassword(encoder.encode(signUpRequest.getPassword()));
      User saved=userRepository.save(newUser);
      return mapper.map(saved,UserDto.class);
    }

    public String[] login(@Valid LoginRequestDto loginRequestDto) {
        Authentication authentication= manager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(),loginRequestDto.getPassword()));

        User user=(User)authentication.getPrincipal();

        String[] tokens=new String[2];
        tokens[0]=jwtService.generateAccessToken(user);
        tokens[1]=jwtService.generateRefreshToken(user);
        return tokens;
    }

    public String refresh(String refresh) {
        String email = jwtService.extractUsername(refresh);
        User user=userService.getUserByEmail(email);
        return jwtService.generateAccessToken(user);
    }
}
