package com.team5.pyeonjip.global.config;

import com.team5.pyeonjip.global.jwt.CustomLogoutFilter;
import com.team5.pyeonjip.global.jwt.JWTFilter;
import com.team5.pyeonjip.global.jwt.JWTUtil;
import com.team5.pyeonjip.global.jwt.LoginFilter;
import com.team5.pyeonjip.user.repository.RefreshRepository;
import com.team5.pyeonjip.user.service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final ReissueService reissueService;


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager Bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                                CorsConfiguration configuration = new CorsConfiguration();

                                // 데이터를 보내는 3000번 포트를 허용
                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));

                                // 모든 메서드 허용
                                configuration.setAllowedMethods(Collections.singletonList("*"));

                                configuration.setAllowCredentials(true);

                                // 허용할 헤더
                                configuration.setAllowedHeaders(Collections.singletonList("*"));

                                // 허용 시간
                                configuration.setMaxAge(3600L);

                                // Authorization 헤더 노출
                                configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                                return configuration;
                            }
                        }));
        // csrf 비활성화. JWT는 세션을 stateless로 관리하기 때문
        http
                .csrf((auth) -> auth.disable());

        // Form Login 비활성화
        http
                .formLogin((auth) -> auth.disable());

        // http basic 인증 방식 비활성화
        http
                .httpBasic((auth) -> auth.disable());

        // 경로별 인가
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/signup").permitAll()
                        // 관리자만 접근 가능
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 토큰 리이슈
                        .requestMatchers("/api/user/reissue").permitAll()

                        /* 권한 처리하기 */
                        /*
                        * 1번의 permitAll()은 모든 사용자가 사용 가능합니다.
                        * 2번의 authenticated()는 로그인한 사용자만 사용 가능합니다.
                        * 3번은 2번이 잘 되지 않을 경우 사용하시면 되겠습니다.
                        * 관리자만 접근 가능한 api는 위에 정의해뒀습니다!
                        *
                        * 고맙습니다 ,,,
                        */

                        /* 장바구니 */
                        .requestMatchers("/api/cart/**").permitAll()                     // 1
                        .requestMatchers("/api/cart/sync").authenticated()                 // 2
                        //.requestMatchers("/api/cart/**").hasAnyRole("ADMIN", "USER")     // 3

                        /*쿠폰*/
                        .requestMatchers("/api/coupon/**").permitAll()
                        .requestMatchers("api/coupon/custom/**").hasRole("ADMIN")

                        /*댓글*/
                        .requestMatchers("/api/comments/product/**").permitAll()
                        .requestMatchers("/api/comments/product-rating/**").permitAll()
                        .requestMatchers("/api/comments/**").authenticated()



                        /* 채팅 */
                        //.requestMatchers("/api/chat/**").permitAll()                      // 1
                        //.requestMatchers("/api/chat/**").hasRole("USER")                  // 2
                        //.requestMatchers("/api/chat/**").hasAnyRole("ADMIN", "USER")      // 3

                        /* 카테고리 */
                        //.requestMatchers("/api/category/**").permitAll()                  // 1
                        //.requestMatchers("/api/category/**").hasRole("USER")              // 2
                        //.requestMatchers("/api/category/**").hasAnyRole("ADMIN", "USER")  // 3

                        /* 주문 */
                        //.requestMatchers("/api/order/**").permitAll()                     // 1
                        //.requestMatchers("/api/order/**").hasRole("USER")                 // 2
                        //.requestMatchers("/api/order/**").hasAnyRole("ADMIN", "USER")     // 3

                        /* 유저 */
                        //.requestMatchers("/api/user/**").permitAll()                      // 1
                        //.requestMatchers("/api/user/**").hasRole("USER")                  // 2
                        //.requestMatchers("/api/user/**").hasAnyRole("ADMIN", "USER")      // 3

                        /* 상품 */
                        //.requestMatchers("/api/product/**").permitAll()                    // 1
                        //.requestMatchers("/api/product/**").hasRole("USER")                // 2
                        //.requestMatchers("/api/product/**").hasAnyRole("ADMIN", "USER")    // 3

                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/chat/waiting-rooms").hasRole("ADMIN")

                        // 위에서 처리되지 않은 api 요청은 어떻게 처리할 것인지.
                        // 우선 1번으로 해두시는 게 테스트에 편하실 것 같습니다.
                        // 1. 모두 허용, 2. 인증된 사용자만 사용 가능
                        .anyRequest().permitAll());         // 1. 모두 허용
//                        .anyRequest().authenticated());   // 2. 인증 필요

        // 필터 등록
//      JWTFilter
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
//      LoginFilter
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository, reissueService), UsernamePasswordAuthenticationFilter.class);
//      LogoutFilter
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);
        // 세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
