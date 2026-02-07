package com.learning.courseplatform.exception;

public class ConflictException extends RuntimeException {
    private String error;
    
    public ConflictException(String message) {
        super(message);
        this.error = "Conflict";
    }
    
    public ConflictException(String error, String message) {
        super(message);
        this.error = error;
    }
    
    public String getError() {
        return error;
    }
}
