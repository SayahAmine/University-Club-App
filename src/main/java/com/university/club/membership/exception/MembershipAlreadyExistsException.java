package com.university.club.membership.exception;

public class MembershipAlreadyExistsException extends RuntimeException {

    public MembershipAlreadyExistsException() {
        super("This user already has a club membership.");
    }
}