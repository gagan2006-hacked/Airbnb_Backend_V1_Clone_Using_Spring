package com.codingshuttle.projects.airBnbApp.controller;

import com.codingshuttle.projects.airBnbApp.dto.LoginRequestDto;
import com.codingshuttle.projects.airBnbApp.dto.SignUpRequestDto;
import com.codingshuttle.projects.airBnbApp.dto.UserDto;
import com.codingshuttle.projects.airBnbApp.service.impl.AuthUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthUserService authUserService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody @Valid SignUpRequestDto signUpRequest){
        return ResponseEntity.ok(authUserService.signUp(signUpRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response){
        String[] tokens=authUserService.login(loginRequestDto);
        Cookie access=new Cookie("access",tokens[0]);
        Cookie refresh=new Cookie("refresh",tokens[1]);

        access.setPath("/");
        refresh.setPath("/");

        access.setMaxAge(60*10);
        refresh.setMaxAge(90 * 24 * 60 * 60);

        response.addCookie(access);
        response.addCookie(refresh);

        log.info("access:{}",tokens[0]);
        log.info("refresh:{}",tokens[1]);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest request, HttpServletResponse response){
        String refresh= Arrays.stream(request.getCookies()).filter(cookie -> "refresh".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(()->new AuthorizationServiceException("Refresh Token not found"));
        String access=authUserService.refresh(refresh);
        Cookie accessCookie=new Cookie("access",access);

        accessCookie.setPath("/");
        accessCookie.setMaxAge(600);

        response.addCookie(accessCookie);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
