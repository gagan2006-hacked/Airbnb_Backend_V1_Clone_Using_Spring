package com.codingshuttle.projects.airBnbApp.Filter;

import com.codingshuttle.projects.airBnbApp.service.impl.JwtService;
import com.codingshuttle.projects.airBnbApp.entity.User;
import com.codingshuttle.projects.airBnbApp.service.Interface.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String tokenHeader="";

        if (cookies == null) {
            log.warn("No cookies received for {}", request.getRequestURI());
            filterChain.doFilter(request,response);
            return;
        } else {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("access")){
                    tokenHeader=cookie.getValue();
                    break;
                }
            }
        }

//        if (tokenHeader==null || !tokenHeader.startsWith("Bearer ")){
//
//        }

        String token=(tokenHeader.isBlank()?null:tokenHeader);
        try {
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userService.getUserByEmail(jwtService.extractUsername(token));
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                auth.setDetails(
                        new WebAuthenticationDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        catch (JwtException e){
            handlerExceptionResolver.resolveException(request,response,null,e);
        }
        filterChain.doFilter(request,response);
    }
}
