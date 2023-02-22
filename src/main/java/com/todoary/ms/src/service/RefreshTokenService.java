package com.todoary.ms.src.service;

import com.todoary.ms.src.domain.token.RefreshToken;
import com.todoary.ms.src.common.exception.TodoaryException;
import com.todoary.ms.src.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.todoary.ms.src.common.response.BaseResponseStatus.USERS_REFRESH_TOKEN_NOT_EXISTS;

@RequiredArgsConstructor
@Transactional
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(readOnly = true)
    public Boolean existsByCode(String code) {
        return refreshTokenRepository.existsByCode(code);
    }

    @Transactional
    public Long save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken findByMemberId(Long memberId) {
        return refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new TodoaryException(USERS_REFRESH_TOKEN_NOT_EXISTS));
    }
}
