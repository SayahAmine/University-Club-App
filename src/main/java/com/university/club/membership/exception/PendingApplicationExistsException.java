package com.university.club.membership.exception;

public class PendingApplicationExistsException extends RuntimeException {

    public PendingApplicationExistsException() {
        super("You already have a pending membership application.");
    }
}