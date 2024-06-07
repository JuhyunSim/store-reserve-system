package com.zerobase.partner.security.config;

import com.zerobase.domain.security.common.UserType;
import com.zerobase.partner.security.encrypt.Aes256Utils;
import com.zerobase.partner.service.CustomerService;
import com.zerobase.partner.service.PartnerService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.lang.Maps;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtAuthProvider {

    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    private static final Logger log = LoggerFactory.getLogger(JwtAuthProvider.class);
    private final long tokenValidTime = 1000L * 60 * 60;

    private final PartnerService partnerService;
    private final CustomerService customerService;

    Aes256Utils aes256Utils = new Aes256Utils();

    private SecretKey getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     *  토큰 생성
     */

    public String generateToken(String email, Long id, UserType userType)
            throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        Claims claims = Jwts.claims()
                .add("roles", userType)
                .subject(aes256Utils.encrypt(email))
                .id(aes256Utils.encrypt(id.toString())).build();

        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + tokenValidTime);

        log.info("expires at : {}", expiresAt);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiresAt) // 토큰 유효 기간 1시간
                .signWith(getSignKey())
                .compact();
    }

    /**
     * 토큰의 유효성 확인
     */

    public Authentication getAuthentication(String jwt)
            throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        log.info("Jwt : {}", jwt);

        UserType userType = Jwts.parser()
                .verifyWith(getSignKey())
                .json(new JacksonDeserializer(Maps.of("roles", UserType.class).build()))
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .get("roles", UserType.class);

        UserDetails userDetails = null;
        if (userType == UserType.PARTNER) {
            userDetails =
                    this.partnerService.loadUserByUsername(this.getEmail(jwt).trim());
        } else {
            //usertype == CUSTOMER
            userDetails =
                    this.customerService.loadUserByUsername(this.getEmail(jwt).trim());
        }

        return new UsernamePasswordAuthenticationToken(userDetails,
                "", userDetails.getAuthorities());
    }

    //token에 email 확인
    public String getEmail(String token)
            throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        log.info("cipher text : {}", this.parseClaims(token).getSubject());
        log.info("email: {}", aes256Utils.decrypt(this.parseClaims(token).getSubject()));
        return aes256Utils.decrypt(this.parseClaims(token).getSubject());
    }

    //token에 id 확인
    public Long getId(String token)
            throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        return Long.valueOf(aes256Utils.decrypt(
                this.parseClaims(token).getId()));
    }


    //토큰 기간 만료 확인
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        log.info("Validate token: {}", token);


            Claims claims = this.parseClaims(token);
            log.info("expiration: {}", claims.getExpiration());
            log.info("expiration result: {}", !claims.getExpiration().before(new Date()));
            return !claims.getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
