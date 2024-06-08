package com.zerobase.reserve.config;

import com.zerobase.domain.security.config.JwtAuthProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(JwtAuthProvider.class)
//@ComponentScan(basePackages = "com.zerobase.reserve")
public class JwtConfig {
}
