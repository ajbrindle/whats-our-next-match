package com.sk7software.nextmatch.exception;

public class UnauthorizedException extends DeviceAddressClientException {

    public UnauthorizedException(String message, Exception e) {
        super(message, e);
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}