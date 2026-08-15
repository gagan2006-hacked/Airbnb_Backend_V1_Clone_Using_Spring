package com.codingshuttle.projects.airBnbApp.config;

import com.codingshuttle.projects.airBnbApp.util.RazorpayProperties;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class PaymentConfig {

    private final RazorpayProperties properties;

    @Bean
    RazorpayClient razorpayClient() throws RazorpayException {
        return new RazorpayClient(properties.getKey(), properties.getSecret());
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
// TODO Remove in Proudution
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:63342", "http://127.0.0.1:5500")
                        .allowedMethods("*")
                        .allowCredentials(true);
            }
        };
    }
}
