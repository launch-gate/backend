package com.launchgate.common;

public class NotFoundException extends DomainException {

    public NotFoundException(String message) {
        super("not_found", message);
    }
}
