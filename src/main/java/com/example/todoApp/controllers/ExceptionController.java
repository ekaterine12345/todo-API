package com.example.todoApp.controllers;

import com.example.todoApp.dtos.ApiResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionController {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ApiResponse handleUnexpectedException(Exception e){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.addError("unknown exception = ", e.getMessage());
        return apiResponse;
    }
}
