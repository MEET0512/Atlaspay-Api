package com.atlaspay.api.utils;

import com.atlaspay.api.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    /**
     * Create successful response
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data, HttpStatus status) {
        ApiResponse<T> apiResponse = (ApiResponse<T>) ApiResponse.builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
        return new ResponseEntity<>(apiResponse, status);
    }

    /**
     * Create successful response with OK status
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return success(message, data, HttpStatus.OK);
    }

    /**
     * Create successful response with CREATED status
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return success(message, data, HttpStatus.CREATED);
    }

    /**
     * Create error response
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, String errorCode, HttpStatus status) {
        ApiResponse<T> apiResponse = (ApiResponse<T>) ApiResponse.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
        return new ResponseEntity<>(apiResponse, status);
    }

    /**
     * Create error response with BAD_REQUEST status
     */
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
        return error(message, "BAD_REQUEST", HttpStatus.BAD_REQUEST);
    }

    /**
     * Create error response with UNAUTHORIZED status
     */
    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return error(message, "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Create error response with NOT_FOUND status
     */
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return error(message, "NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    /**
     * Create error response with CONFLICT status
     */
    public static <T> ResponseEntity<ApiResponse<T>> conflict(String message) {
        return error(message, "CONFLICT", HttpStatus.CONFLICT);
    }
}
