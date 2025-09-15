package com.loopca.app.repository;

import com.loopca.app.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    //중복 체크
    boolean existsByMemberId(String memberId);
    // 로그인 시 조회
    Optional<Member> findByMemberId(String memberId);
}