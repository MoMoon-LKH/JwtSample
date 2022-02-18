package com.group.jwtSample.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.lang.reflect.Array;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "auth";

    private final String secret;

    private final long tokenValidityInMilliseconds;

    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds * 1000;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes); // 디코드한 변수를 키에 할당
    } // 빈이 생성이 되고 주입을 받은 후에 실행되는 메서드


    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        // stream 객체를 하나의 String 객체로 만드는 코드 -> ,로 구분자로 사용중 ex) apple, banana , ....


        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName()) // 제목 설정
                .claim(AUTHORITIES_KEY,authorities) // Payload에 들어갈 정보
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity) // 유효시간 설정
                .compact();
    } // 토큰 생성


    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody(); // 토큰을 이용해서 클레임을 만듬

        Collection<? extends GrantedAuthority> authorities =  // <? extends !> ! 하위 클래스를 모두 허용하는 표현
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()); // stream 객체 List로 만드는 코드

        User principal = new User(claims.getSubject(), "", authorities); // 권한 정보로 유저 객체 생성

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    } // 인증 정보를 주는 역할


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원하지않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("잘못된 JWT 토큰입니다.");
        }

        return false;
    } // 토큰 유효성 검사


}
