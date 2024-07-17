package com.example.authservice.grpc;

import com.example.authservice.annotation.GrpcExceptionHandler;
import com.example.authservice.constants.TokenConstants;
import com.example.authservice.service.AuthService;
import com.example.authservice.util.GrpcResponseHelper;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.grpc.*;

@GrpcService
public class AuthServiceTmpl extends AuthServiceGrpc.AuthServiceImplBase {
    @Autowired
    private AuthService authService;

    @Autowired
    private GrpcResponseHelper grpcResponseHelper;

    @Override
    @GrpcExceptionHandler
    public void generateAccessToken(GenerateAccessTokenRequest request, StreamObserver<Response> responseObserver) {
        String token = authService.generateToken(request.getUserId());
        grpcResponseHelper.sendJsonResponse(TokenConstants.ACCESS_TOKEN, token, responseObserver);
    }

    @Override
    @GrpcExceptionHandler
    public void logout(LogoutRequest request, StreamObserver<Response> responseObserver) {
        authService.logout(request.getUserId());
        grpcResponseHelper.sendJsonResponse("message", "User logged out successfully.", responseObserver);
    }

    @Override
    @GrpcExceptionHandler
    public void refreshToken(RefreshTokenRequest request, StreamObserver<Response> responseObserver) {
        String newAccessToken = authService.refreshAccessToken(request.getUserId());
        grpcResponseHelper.sendJsonResponse(TokenConstants.ACCESS_TOKEN, newAccessToken, responseObserver);
    }
}
