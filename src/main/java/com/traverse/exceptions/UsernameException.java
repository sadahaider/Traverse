package com.traverse.exceptions;


public class UsernameException extends Exception {

    private String reason;

    public UsernameException(String reason){
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

}
