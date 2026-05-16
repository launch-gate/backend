package com.launchgate.common;

public class LaunchGateException extends DomainException {

    public LaunchGateException(String message) {
        super(ErrorCode.applicationErrorCode, message);
    }
}
