package com.codingshuttle.projects.airBnbApp.Exception;

import com.codingshuttle.projects.airBnbApp.Exception.Enum.HttpMethod;
import com.codingshuttle.projects.airBnbApp.Exception.except.*;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor

public class GlobalExceptionHandler {

    private Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError<?>> validationErrorHandling(MethodArgumentNotValidException ex, HttpServletRequest request){
        String path = request.getRequestURI();
        FieldError fieldError=ex.getFieldError();
        logger.info("Error at (MethodArgumentNotValidException.class");
        ApiError<?> error=ApiError.builder()
                .success(false)
                .message("Validation failed")
                .error(fieldError != null
                        ? fieldError.getDefaultMessage()
                        : null)
                .dateTime(LocalDateTime.now())
                .info(HttpsInfo.builder()
                        .path(path)
                        .method(HttpMethod.getMethod(request.getMethod())).build()).build();
        return new ResponseEntity<ApiError<?>>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError<?>> handleNotFound(ResourceNotFoundException ex,HttpServletRequest request){
        String path = request.getRequestURI();
        logger.error("Error at ResourceNotFoundException.class ",ex.getCause());
        ApiError<?>error=ApiError.builder()
                .success(false)
                .message("Resource Not Found")
                .error(ex.getErrorMessage())
                .dateTime(LocalDateTime.now())
                .info(HttpsInfo.builder()
                        .path(path)
                        .method((HttpMethod.getMethod(request.getMethod())))
                        .build())
                .build();
        return new ResponseEntity<ApiError<?>>(error, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ApiError<?>> handleDuplicateEntry(DuplicateEntryException ex,HttpServletRequest request){
        String path = request.getRequestURI();
        logger.info("Error at DuplicateEntryException.class");
        ApiError<?>error=ApiError.builder()
                .success(false)
                .message("Duplicate entry Requested")
                .error(ex.getErrorMessage())
                .dateTime(LocalDateTime.now())
                .info(HttpsInfo.builder()
                        .path(path)
                        .method((HttpMethod.getMethod(request.getMethod())))
                        .build())
                .build();
        return new ResponseEntity<ApiError<?>>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UnAuthorisedException.class)
    public ResponseEntity<ApiError<?>> handleUnAuthorisedExceptionEntry(UnAuthorisedException ex,HttpServletRequest request){
        String path = request.getRequestURI();
        logger.info("Error at UnAuthorisedException.class");
        ApiError<?>error=ApiError.builder()
                .success(false)
                .message("UnAuthorised")
                .error(ex.getLocalizedMessage())
                .dateTime(LocalDateTime.now())
                .info(HttpsInfo.builder()
                        .path(path)
                        .method((HttpMethod.getMethod(request.getMethod())))
                        .build())
                .build();
        return new ResponseEntity<ApiError<?>>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError<?>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        logger.info("Error at HttpMessageNotReadableException.class");

        return ResponseEntity.badRequest()
                .body(ApiError.builder()
                        .success(false)
                        .message("Invalid value")
                        .error(ex.getMessage())
                        .dateTime(LocalDateTime.now())
                        .info(HttpsInfo.builder()
                                .path(request.getRequestURI())
                                .method(HttpMethod.getMethod(request.getMethod()))
                                .build())
                        .build());


    }
//    log.error("Error At DataBase Level :{} when Calling Method: deleteHotelById()",e.getLocalizedMessage());

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError<?>> handleAuthenticationException(AuthenticationException ex,HttpServletRequest request) {
        ApiError apiError = ApiError.builder()
                .success(false)
                .error(ex.getLocalizedMessage())
                .message("Authentication Failed")
                .dateTime(LocalDateTime.now())
                .info(
                        HttpsInfo.builder()
                                .path(request.getRequestURI())
                                .method(HttpMethod.getMethod(request.getMethod()))
                                .build()
                ).build();
        return ResponseEntity.status(401)
                .body(apiError);
    }


    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError<?>> handleJwtException(JwtException ex,HttpServletRequest request) {
        ApiError apiError = ApiError.builder()
                .success(false)
                .error(ex.getLocalizedMessage())
                .message("Jwt Failed")
                .dateTime(LocalDateTime.now())
                .info(
                        HttpsInfo.builder()
                                .path(request.getRequestURI())
                                .method(HttpMethod.getMethod(request.getMethod()))
                                .build()
                ).build();
        return ResponseEntity.status(401)
                .body(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError<?>> handleAccessDeniedException(AccessDeniedException ex,HttpServletRequest request) {
        ApiError apiError = ApiError.builder()
                .success(false)
                .error(ex.getLocalizedMessage())
                .message(ex.getMessage())
                .dateTime(LocalDateTime.now())
                .info(
                        HttpsInfo.builder()
                                .path(request.getRequestURI())
                                .method(HttpMethod.getMethod(request.getMethod()))
                                .build()
                ).build();
        return ResponseEntity.status(403)
                .body(apiError);
    }







    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiError<?>> handleDataAccessException( HttpServletRequest request,DataAccessException e){
        logger.error("Error At DataBase Level :{}",e.getLocalizedMessage());
        logger.error("Error Cause :{}",e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.builder()
                        .success(false)
                        .message("DataBase Error")
                        .error(e.getLocalizedMessage())
                        .dateTime(LocalDateTime.now())
                        .info(HttpsInfo.builder()
                                .path(request.getRequestURI())
                                .method(HttpMethod.getMethod(request.getMethod()))
                                .build())
                        .build()
                );
    }


    @ExceptionHandler(BookingExpiredException.class)
    public ResponseEntity<ApiError<?>> handleBookingExpiredException(
            BookingExpiredException ex,
            HttpServletRequest request) {
        logger.error("Error at BookingExpiredException.class ",ex.getCause());
        logger.error("Error Cause at BookingExpiredException:{}",ex.getMessage());
        return  ResponseEntity
                .status(410)
                .body(ApiError.builder()
                        .success(false)
                        .message("Error")
                        .error(ex.getMessage())
                        .dateTime(LocalDateTime.now())
                        .info(HttpsInfo.builder()
                                .path(request.getRequestURI())
                                .method((HttpMethod.getMethod(request.getMethod())))
                                .build())
                        .build());
    }


    @ExceptionHandler(InvalidPaymentDetails.class)
    public ResponseEntity<ApiError<?>> handleInvalidPaymentDetailsException(
            InvalidPaymentDetails ex,
            HttpServletRequest request) {
        logger.error("Error at InvalidPaymentDetails.class ",ex.getCause());
        logger.error("Error Cause at InvalidPaymentDetails:{}",ex.getMessage());
//
        return  ResponseEntity
                .status(400)
                .body(ApiError.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .error(ex.getLocalizedMessage())
                        .dateTime(LocalDateTime.now())
                        .info(HttpsInfo.builder()
                                .path(request.getRequestURI())
                                .method((HttpMethod.getMethod(request.getMethod())))
                                .build())
                        .build());
    }


    @ExceptionHandler(PaymentGateWayException.class)
    public ResponseEntity<ApiError<?>> handleBookingExpiredException(
            PaymentGateWayException ex,
            HttpServletRequest request) {
        logger.error("Error at PaymentException.class ",ex.getCause());
        logger.error("Error Cause at PaymentException:{}",ex.getMessage());
//
        return  ResponseEntity
                .status(ex.getStatus())
                .body(ApiError.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .error(ex.getLocalizedMessage())
                        .dateTime(LocalDateTime.now())
                        .info(HttpsInfo.builder()
                                .path(request.getRequestURI())
                                .method((HttpMethod.getMethod(request.getMethod())))
                                .build())
                        .build());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError<?>> handleBookingExpiredException(
            IllegalStateException ex,
            HttpServletRequest request) {
        logger.error("Error at IllegalStateException.class ",ex.getCause());
        logger.error("Error Cause at IllegalStateException:{}",ex.getMessage());
//
        return  ResponseEntity
                .status(409)
                .body(ApiError.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .error(ex.getLocalizedMessage())
                        .dateTime(LocalDateTime.now())
                        .info(HttpsInfo.builder()
                                .path(request.getRequestURI())
                                .method((HttpMethod.getMethod(request.getMethod())))
                                .build())
                        .build());
    }




    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError<?>> handleException(
            Exception ex,
            HttpServletRequest request) {
        logger.error("Error at Exception.class ",ex.getCause());
        logger.error("Error Cause :{}",ex.getMessage());
        return  ResponseEntity
                .status(500)
                .body(ApiError.builder()
                        .success(false)
                        .message("Error")
                        .error(ex.getMessage())
                        .dateTime(LocalDateTime.now())
                        .info(HttpsInfo.builder()
                                .path(request.getRequestURI())
                                .method((HttpMethod.getMethod(request.getMethod())))
                                .build())
                        .build());
    }
}
