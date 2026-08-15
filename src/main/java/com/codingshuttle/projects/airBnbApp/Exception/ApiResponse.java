package com.codingshuttle.projects.airBnbApp.Exception;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse <T>{
    /*
    "success": true,
      "message": "User fetched successfully",
      "data": {
        "id": 1,
        "name": "Gagan"
      },
      "timestamp": "2026-05-31T12:30:00Z"
    * */
    private Boolean success;
    private String message;
    private T  data;
    private LocalDateTime dateTime;
    private HttpsInfo info;
}
