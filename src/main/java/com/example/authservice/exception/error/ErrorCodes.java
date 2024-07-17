package com.example.authservice.exception.error;

import io.grpc.Status;

public class ErrorCodes {
    public static final Status.Code INVALID_REFRESH_TOKEN_CODE = Status.Code.UNAUTHENTICATED;
    public static final Status.Code REFRESH_TOKEN_NOT_FOUND_CODE = Status.Code.NOT_FOUND;
}