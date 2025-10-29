package ru.programm_shcool.quizizz.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.programm_shcool.quizizz.dto.util.StandardResponse;

public class AbstractController {
    protected ResponseEntity<Object> getStandardResponse(String message) {
        return getErrorResponse(message, 200);
    }

    protected ResponseEntity<Object> getErrorResponse(String message, int code) {
        StandardResponse response = StandardResponse.builder()
                .status(code)
                .message(message)
                .build();

        return ResponseEntity.status(code).body(response);
    }

    protected ResponseEntity<Object> getResponse(Object response) {
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
