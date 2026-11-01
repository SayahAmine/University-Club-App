package com.university.club.identity.entity;

public enum UserStatus {
    ACTIVE, //Can authenticate.
    DISABLED, //Administratively deactivated
    LOCKED //Temporarily or permanently blocked
}