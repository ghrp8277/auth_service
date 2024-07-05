package com.example.authservice.service;

import com.example.authservice.entity.RefreshToken;
import com.example.authservice.repository.RefreshTokenRepository;
import com.example.authservice.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public String generateToken(Long userId) {
        String accessToken = generateAccessToken(userId);
        generateRefreshToken(userId);

        return accessToken;
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    public String generateAccessToken(Long userId) {
        return jwtUtil.generateToken(userId);
    }

    // 리프레시 토큰 생성 및 저장 메서드
    public void generateRefreshToken(Long userId) {
        String refreshToken = jwtUtil.generateRefreshToken(userId);
        Date expirationDate = jwtUtil.getClaimsFromToken(refreshToken).getExpiration();
        saveRefreshToken(refreshToken, userId, expirationDate);
    }

    // 기존 리프레시 토큰을 확인하고 만료 여부를 체크하는 메서드
    private Optional<String> checkExistingRefreshToken(Long userId) {
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUserId(userId);

        if (existingTokenOpt.isPresent()) {
            RefreshToken existingToken = existingTokenOpt.get();
            if (!jwtUtil.isTokenExpired(existingToken.getToken())) {
                return Optional.of(existingToken.getToken());
            }
        }
        return Optional.empty();
    }

    // 만료된 액세스 토큰을 검증하고, 리프레시 토큰으로 새로운 액세스 토큰 발급
    public String refreshAccessToken(Long userId) {
        Optional<String> existingRefreshTokenOpt = checkExistingRefreshToken(userId);

        if (existingRefreshTokenOpt.isPresent()) {
            String refreshToken = existingRefreshTokenOpt.get();

            if (!jwtUtil.isTokenExpired(refreshToken)) {
                return generateAccessToken(userId);
            } else {
                throw new RuntimeException("Invalid refresh token");
            }
        } else {
            throw new RuntimeException("Refresh token not found");
        }
    }

    private void saveRefreshToken(String token, Long userId, Date expirationDate) {
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUserId(userId);

        if (existingTokenOpt.isPresent()) {
            updateRefreshToken(existingTokenOpt.get(), token, expirationDate);
            return;
        }

        LocalDateTime expiresIn = expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUserId(userId);
        refreshToken.setExpiresIn(expiresIn);
        refreshTokenRepository.save(refreshToken);
    }

    private void updateRefreshToken(RefreshToken existingToken, String refreshToken, Date expirationDate) {
        existingToken.setToken(refreshToken);
        existingToken.setExpiresIn(expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        refreshTokenRepository.save(existingToken);
    }
}
