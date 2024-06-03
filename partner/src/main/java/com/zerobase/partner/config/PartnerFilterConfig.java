//package com.zerobase.partner.config;
//
//import com.zerobase.auth.common.UserVo;
//import com.zerobase.auth.config.JwtAuthProvider;
//import com.zerobase.partner.service.PartnerService;
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@RequiredArgsConstructor
//@Component
//    public class PartnerFilterConfig implements Filter {
//    private final JwtAuthProvider jwtAuthProvider;
//    private final PartnerService partnerService;
//
//    @SneakyThrows
//    @Override
//    public void doFilter(ServletRequest servletRequest,
//                         ServletResponse servletResponse,
//                         FilterChain filterChain
//    ) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        String token = request.getHeader("X-Auth-Token");
//        UserVo userVo = jwtAuthProvider.getUserVo(token);
//        if (!jwtAuthProvider.validateToken(token, userVo)) {
//            throw new ServletException("Invalid token");
//        }
//        partnerService.findByIdAndEmail(userVo.getId(), userVo.getEmail())
//                .orElseThrow(() -> new ServletException("Partner not found"));
//        filterChain.doFilter(servletRequest, servletResponse);
//    }
//}