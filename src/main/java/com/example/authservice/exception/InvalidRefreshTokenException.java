package com.example.authservice.exception;

import com.example.authservice.exception.error.ErrorCodes;
import com.example.authservice.exception.error.ErrorMessages;
import com.example.authservice.exception.error.GrpcException;

public class InvalidRefreshTokenException extends GrpcException {
    public InvalidRefreshTokenException() {
        super(ErrorCodes.INVALID_REFRESH_TOKEN_CODE, ErrorMessages.INVALID_REFRESH_TOKEN);
    }
}
