package com.example.todoApp.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public final class ApiResponse {
    private Map<String, Object> data = new HashMap<>();
    private Map<String, Object> errors = new HashMap<>();

    public ApiResponse(String key, Object vale) {
        this.data.put(key, vale);
    }

    public ApiResponse addData(String key, Object vale){
        this.data.put(key, vale);
        return this;
    }

    public ApiResponse addError(String key, Object value){
        this.errors.put(key, value);
        return this;
    }

    public ApiResponse() {
    }
}
