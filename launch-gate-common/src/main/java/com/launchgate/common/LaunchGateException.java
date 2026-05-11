package com.launchgate.common;

public class LaunchGateException extends RuntimeException {

    public LaunchGateException(String message) {
        super(message);
    }

    public LaunchGateException(String message, Throwable th) {
        super(message, th);
    }
}
