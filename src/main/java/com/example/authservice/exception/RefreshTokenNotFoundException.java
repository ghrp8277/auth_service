package com.example.authservice.exception;

import com.example.authservice.exception.error.ErrorCodes;
import com.example.authservice.exception.error.ErrorMessages;
import com.example.authservice.exception.error.GrpcException;

public class RefreshTokenNotFoundException extends GrpcException {
    public RefreshTokenNotFoundException() {
        super(ErrorCodes.REFRESH_TOKEN_NOT_FOUND_CODE, ErrorMessages.REFRESH_TOKEN_NOT_FOUND);
    }
}
