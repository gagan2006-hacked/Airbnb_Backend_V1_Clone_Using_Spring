package com.codingshuttle.projects.airBnbApp.Exception;

import com.codingshuttle.projects.airBnbApp.Exception.Enum.HttpMethod;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ApiResponseHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public  Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiError<?> || body instanceof ApiResponse<?>)return body;
            ApiResponse<?>apiRes=ApiResponse.builder() // it is say error here
                .success(true)
                .message("Success")
                .data(body)
                .dateTime(LocalDateTime.now())
                .info(HttpsInfo.builder() // it is say error here
                        .path(request.getURI().getPath())
                        .method(HttpMethod.getMethod(request.getMethod().toString()))
                        .build())
                .build();
        return apiRes;
    }
}
