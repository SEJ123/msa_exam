package com.sparta.msa_exam.auth;

import com.sparta.msa_exam.auth.dto.AuthResponseDto;
import com.sparta.msa_exam.auth.dto.SignInRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/auth/signUp")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        User createdUser = authService.signUp(user);
        return ResponseEntity.ok(createdUser);
    }

    // 로그인
    @PostMapping("/auth/signIn")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody SignInRequestDto signInRequest){
        String token = authService.signIn(signInRequest.getUserId(), signInRequest.getPassword());
        return ResponseEntity.ok(new AuthResponseDto(token));
    }
}