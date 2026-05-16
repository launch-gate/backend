package com.launchgate.common;

public class ConflictException extends DomainException {

    public ConflictException(String message) {
        super(ErrorCode.conflictErrorCode, message);
    }
}
