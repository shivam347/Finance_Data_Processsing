package com.zorvyn.dashboard.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* When we want consistent api response so created this class */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    /* Two method created depends on the parameter it will called that */

    public static <T> ApiResponse<T> success(T data){

        return success(data);

    }


    public static <T> ApiResponse<T> success(T data, String message) {

        return ApiResponse.<T>builder()
                  .success(true)
                  .message(message)
                  .data(data)
                  .build();

    }
    
}
