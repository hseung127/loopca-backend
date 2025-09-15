package com.loopca.app.controller;

import com.loopca.app.dto.LoginRequest;
import com.loopca.app.dto.LoginResponse;
import com.loopca.app.dto.MemberDto;
import com.loopca.app.dto.SignupRequest;
import com.loopca.app.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    // 회원가입 → 자동 로그인 응답
    @PostMapping("/signup")
    public ResponseEntity<LoginResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(memberService.signup(request));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(memberService.login(request.getMemberId(), request.getPassword()));
    }
}

