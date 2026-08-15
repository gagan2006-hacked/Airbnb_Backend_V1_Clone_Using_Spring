package com.codingshuttle.projects.airBnbApp.Exception.Enum;

public enum HttpMethod{
    POST,GET,PUT,DELETE,PATCH;

    public static HttpMethod getMethod(String val){
        return HttpMethod.valueOf(val.toUpperCase());
    }
}
