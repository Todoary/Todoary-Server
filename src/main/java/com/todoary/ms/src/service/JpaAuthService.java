package com.todoary.ms.src.service;

import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.token.AccessToken;
import com.todoary.ms.src.domain.token.AuthenticationToken;
import com.todoary.ms.src.domain.token.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class JpaAuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberService memberService;

//    @Transactional(readOnly = true)
//    public Boolean refreshTokenExistsByCode(String code) {
//        return refreshTokenService.existsByCode(code);
//    }

    @Transactional(readOnly = true)
    public Boolean decodableRefreshToken(String refreshTokenCode, Long memberId) throws Exception {
        return memberId == Long.parseLong(jwtTokenProvider.getUserIdFromRefreshToken(refreshTokenCode));
    }

    @Transactional(readOnly = true)
    public Boolean decodableAccessToken(String accessTokenCode, Long memberId) throws Exception {
        return memberId == Long.parseLong(jwtTokenProvider.getUserIdFromAccessToken(accessTokenCode));
    }


    public RefreshToken saveRefreshToken(Member member) {
        RefreshToken refreshToken = new RefreshToken(member, jwtTokenProvider.createRefreshToken(member.getId()));

        refreshTokenService.save(refreshToken);
        return refreshToken;
    }

    public AuthenticationToken issueAuthenticationToken(Long memberId) {
        Member findMember = memberService.findById(memberId);

        AccessToken accessToken = new AccessToken(jwtTokenProvider.createAccessToken(memberId));
        RefreshToken refreshToken = saveRefreshToken(findMember);

        return new AuthenticationToken(accessToken, refreshToken);
    }

    public AuthenticationToken issueAuthenticationToken(String refreshTokenCode) {
        return issueAuthenticationToken(Long.parseLong(jwtTokenProvider.getUserIdFromRefreshToken(refreshTokenCode)));
    }
}
