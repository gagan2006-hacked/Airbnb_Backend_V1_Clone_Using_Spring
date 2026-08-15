package com.codingshuttle.projects.airBnbApp.Exception.except;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder
@AllArgsConstructor
public class DuplicateEntryException extends RuntimeException {
    private String field;
    private String message;

    public DuplicateEntryException(String message) {
        super(message);
        this.message=super.getMessage();
    }
    public Map<String,String> getErrorMessage(){
        HashMap<String,String> map=new HashMap<>();
        map.put(field,message);
        return map;
    }
}
