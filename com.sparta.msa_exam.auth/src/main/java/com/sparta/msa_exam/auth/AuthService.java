package com.sparta.msa_exam.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class AuthService {

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    private final SecretKey secretKey;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(@Value("${service.jwt.secret-key}") String secretKey,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // token 생성
    public String createAccessToken(String userId, UserRoleEnum role) {
        return Jwts.builder()
                .claim("user_id", userId)
                .claim("role", role)
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey)
                .compact();
    }


    public User signUp(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    public String signIn(String userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid user ID or password");
        }

        return createAccessToken(user.getUserId(), user.getRole());
    }
}
//    public String createAccessToken(String user_id) {
//        return Jwts.builder()
//                // 사용자 ID를 클레임으로 설정
//                .claim("user_id", user_id)
//                .claim("role", "ADMIN")
//                // JWT 발행자를 설정
//                .issuer(issuer)
//                // JWT 발행 시간을 현재 시간으로 설정
//                .issuedAt(new Date(System.currentTimeMillis()))
//                // JWT 만료 시간을 설정
//                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
//                // SecretKey를 사용하여 HMAC-SHA512 알고리즘으로 서명
//                .signWith(secretKey, io.jsonwebtoken.SignatureAlgorithm.HS512)
//                // JWT 문자열로 컴팩트하게 변환
//                .compact();
//    }