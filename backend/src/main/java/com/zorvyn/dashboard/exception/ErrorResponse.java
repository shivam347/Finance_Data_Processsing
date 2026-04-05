package com.zorvyn.dashboard.exception;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* Error response class makes all exception to be send in same format to the client or frontend to make 
frontend handler easy , all instance variables are used inside the global exception handler class  */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private boolean success; // tells if request fails , usually false
    private String error;   // main error message like user not found
    private List<String> details;  // for validation error 
    private LocalDateTime timestamp;  // when the error happened
    
}
