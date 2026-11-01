package com.university.club.membership.exception;

public class ProtectedRoleException extends RuntimeException {

    public ProtectedRoleException(String message) {
        super(message);
    }
}