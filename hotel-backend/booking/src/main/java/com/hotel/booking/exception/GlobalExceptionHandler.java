package com.hotel.booking.exception;

import com.hotel.booking.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleAllExceptions(Exception ex) {
        ResponseDTO response = new ResponseDTO();
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()); // 500
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(OurException.class)
    public ResponseEntity<ResponseDTO> handleOurException(OurException ex) {
        ResponseDTO response = new ResponseDTO();
        response.setStatusCode(HttpStatus.BAD_REQUEST.value()); // 400
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
