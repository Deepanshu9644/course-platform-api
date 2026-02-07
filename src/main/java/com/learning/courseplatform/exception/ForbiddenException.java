package com.learning.courseplatform.exception;

public class ForbiddenException extends RuntimeException {
    private String error;
    
    public ForbiddenException(String message) {
        super(message);
        this.error = "Forbidden";
    }
    
    public ForbiddenException(String error, String message) {
        super(message);
        this.error = error;
    }
    
    public String getError() {
        return error;
    }
}
