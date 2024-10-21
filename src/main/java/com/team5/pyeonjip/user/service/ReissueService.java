package com.team5.pyeonjip.user.service;

import com.team5.pyeonjip.global.exception.ErrorCode;
import com.team5.pyeonjip.global.exception.GlobalException;
import com.team5.pyeonjip.global.jwt.JWTUtil;
import com.team5.pyeonjip.user.entity.Refresh;
import com.team5.pyeonjip.user.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;


    // Access 토큰 재발급을 위한 컨트롤러
    public void reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) {

        // Refresh 토큰을 가져온다.
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refreshToken = cookie.getValue();
            }
        }

        // Refresh 토큰이 없는 경우
        if (refreshToken == null) {

            throw new GlobalException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // Refresh 토큰이 만료되었는지 확인
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {

            throw new GlobalException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // Refresh 토큰인지 확인 (발급시 페이로드에 명시된다)
        String category = jwtUtil.getCategory(refreshToken);

        if (!category.equals("refresh")) {

            throw new GlobalException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Refresh 토큰이 DB에 저장되어 있는지 확인
        boolean isExist = refreshRepository.existsByRefresh(refreshToken);

        if (!isExist) {

            throw new GlobalException(ErrorCode.REFRESH_TOKEN_NOT_SAVED);
        }

        /* 여기까지 Refresh 토큰 검증 로직 */

        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // Access, Refresh JWT를 새로 생성.
        String newAccessToken = jwtUtil.createJwt("access", email, role, 600000L);
        String newRefreshToken = jwtUtil.createJwt("refresh", email, role, 86400000L);

        // Refresh 토큰을 생성한 후, DB에 저장된 기존의 토큰은 삭제하고 새로운 토큰을 저장한다.
        refreshRepository.deleteByRefresh(refreshToken);
        addRefresh(email, newRefreshToken, 86400000L);

        // 삭제 대상 토큰이 쿠키에 포함되는 문제가 있어, 명시적으로 삭제하는 코드를 추가
        Cookie deleteOldRefreshToken = new Cookie("refresh", null);
        deleteOldRefreshToken.setMaxAge(0);
        deleteOldRefreshToken.setPath("/");
        deleteOldRefreshToken.setHttpOnly(true);

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        response.addCookie(deleteOldRefreshToken);
        response.addCookie(createCookie("refresh", newRefreshToken));
    }


    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);

        // https 통신 시
        // cookie.setSecure(true);

        // 쿠키가 적용될 범위
        // cookie.setPath("/");

        // js 등에서 쿠키에 접근하지 못하도록.
        cookie.setHttpOnly(true);

        return cookie;
    }


    private void addRefresh(String email, String refresh, Long expiredMs) {

        // 만료일 설정
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh newRefresh = new Refresh();
        newRefresh.setEmail(email);
        newRefresh.setRefresh(refresh);
        newRefresh.setExpiration(date.toString());

        refreshRepository.save(newRefresh);
    }
}
