package com.loopca.app.controller;

import com.loopca.app.dto.CardGroupRequest;
import com.loopca.app.dto.CardGroupResponse;
import com.loopca.app.entity.CardGroup;
import com.loopca.app.security.JwtTokenProvider;
import com.loopca.app.service.CardGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cardGroup")
@RequiredArgsConstructor
public class CardGroupController {

    private final CardGroupService groupService;

    // 새 그룹 생성
    @PostMapping
    public ResponseEntity<CardGroup> createGroup(
            @AuthenticationPrincipal Long memberIdx, // JwtFilter에서 넣은 memberId 꺼냄
            @RequestBody CardGroupRequest request
    ) {
        CardGroup group = groupService.createCardGroup(request, memberIdx);
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    // 로그인된 아이디의 그룹 리스트 조회
    @GetMapping
    public ResponseEntity<List<CardGroupResponse>> getGroups(
            @AuthenticationPrincipal Long memberIdx
    ) {
        List<CardGroupResponse> groups = groupService.getGroupsByMemberIdx(memberIdx);
        return ResponseEntity.ok(groups); // 200 OK
    }



/*
    // 특정 그룹 조회
    @GetMapping("/{groupId}")
    public ResponseEntity<CardGroup> getGroup(
            @RequestHeader("Authorization") String token,
            @PathVariable Long groupId
    ) {
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long memberId = jwtTokenProvider.getMemberId(token);
        CardGroup group = groupService.getGroup(groupId, memberId);
        return ResponseEntity.ok(group);
    }

    // 그룹 삭제
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
            @RequestHeader("Authorization") String token,
            @PathVariable Long groupId
    ) {
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long memberId = jwtTokenProvider.getMemberId(token);
        groupService.deleteGroup(groupId, memberId);
        return ResponseEntity.noContent().build();
    }

    */
}
