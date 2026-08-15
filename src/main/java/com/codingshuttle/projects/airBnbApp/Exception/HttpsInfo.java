package com.codingshuttle.projects.airBnbApp.Exception;


import com.codingshuttle.projects.airBnbApp.Exception.Enum.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HttpsInfo {
    private String path;
    private HttpMethod method;
}
