package com.traverse.exceptions;

/**
 * Created by Me on 10/5/2017.
 */
public class UserDoesNotExistException extends Exception {

    private String reason;

    public UserDoesNotExistException(String reason){
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

}