package com.example.boopoom.exception;

public class NotEnoughPointsException extends RuntimeException {

    public NotEnoughPointsException(){
    }
    public NotEnoughPointsException(String message) {
        super(message);
    }


    public NotEnoughPointsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughPointsException(Throwable cause) {
        super(cause);
    }

}
