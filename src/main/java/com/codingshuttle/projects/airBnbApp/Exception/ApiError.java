package com.codingshuttle.projects.airBnbApp.Exception;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError<T> {
    private Boolean success;
    private String message;
    private T  error;
    private LocalDateTime dateTime;
    private HttpsInfo info;
}
