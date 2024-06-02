package com.zerobase.partner.config;

import com.zerobase.partner.security.config.JwtAuthProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //api 요청 시, 토큰 포함여부 확인, 토큰의 유효성 검사

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer "; // jwt 토큰 사용 시, 토큰 타입
    private final JwtAuthProvider jwtAuthProvider;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("Filtering request URI: {}", requestURI);
//        if (requestURI.startsWith("/signup") || requestURI.startsWith("/signin") || requestURI.startsWith("/v3/api-docs")) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        String token = this.resolveToken(request);
        if (StringUtils.hasText(token) && this.jwtAuthProvider.validateToken(token)) {
            Authentication authentication = this.jwtAuthProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info(String.format("[%s] -> %s ", this.jwtAuthProvider.getEmail(token), request.getRequestURI()));
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

}
