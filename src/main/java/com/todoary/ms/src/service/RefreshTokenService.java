package com.todoary.ms.src.service;

import com.todoary.ms.src.domain.token.RefreshToken;
import com.todoary.ms.src.exception.common.TodoaryException;
import com.todoary.ms.src.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.todoary.ms.util.BaseResponseStatus.USERS_REFRESH_TOKEN_NOT_EXISTS;

@RequiredArgsConstructor
@Transactional
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberService memberService;

    @Transactional(readOnly = true)
    public Boolean existsByCode(String code) {
        return refreshTokenRepository.existsByCode(code);
    }

    public Long save(RefreshToken refreshToken) {
        if (memberService.existsByRefreshToken(refreshToken)) {
            updateCode(refreshToken);
        }
        return refreshTokenRepository.save(refreshToken);
    }

    private void updateCode(RefreshToken refreshToken) {
        memberService.findById(refreshToken.getMember().getId());
    }

    public RefreshToken findByMemberId(Long memberId) {
        return refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new TodoaryException(USERS_REFRESH_TOKEN_NOT_EXISTS));
    }
}
