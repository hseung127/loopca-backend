package com.loopca.app.service;

import com.loopca.app.dto.LoginResponse;
import com.loopca.app.dto.MemberDto;
import com.loopca.app.dto.SignupRequest;
import com.loopca.app.entity.Member;
import com.loopca.app.exception.BusinessException;
import com.loopca.app.exception.ErrorCode;
import com.loopca.app.repository.MemberRepository;
import com.loopca.app.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse signup(SignupRequest request) {
        // 1. 중복체크
        if (memberRepository.existsByMemberId(request.getMemberId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_MEMBER);
        }

        // 2. 비밀번호 해싱
        String encodedPw = passwordEncoder.encode(request.getPassword());

        // 3. 엔티티 생성 및 저장
        Member member = Member.builder()
                .memberId(request.getMemberId())
                .password(encodedPw)
                .nickname(request.getNickname())
                .points(0)
                .build();

        Member saved = memberRepository.save(member);

        // 회원가입 후 자동 로그인
        return doLogin(request.getMemberId(), request.getPassword());
    }

    // 로그인
    public LoginResponse login(String memberId, String rawPassword) {
        return doLogin(memberId, rawPassword);
    }

    // 공통 로그인로직 (JWT 발급)
    public LoginResponse doLogin(String memberId, String rawPassword) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        String token = jwtTokenProvider.createToken(member.getIdx());

        MemberDto dto = MemberDto.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .points(member.getPoints())
                .build();

        return new LoginResponse(token, dto);
    }










}
