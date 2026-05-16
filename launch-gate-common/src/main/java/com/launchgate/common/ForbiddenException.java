package com.launchgate.common;

public class ForbiddenException extends DomainException {

    public ForbiddenException(String message) {
        super(ErrorCode.forbiddenErrorCode, message);
    }
}
