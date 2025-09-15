package com.loopca.app.service;

import com.loopca.app.dto.CardGroupRequest;
import com.loopca.app.dto.CardGroupResponse;
import com.loopca.app.entity.CardGroup;
import com.loopca.app.entity.FlashCard;
import com.loopca.app.entity.Member;
import com.loopca.app.repository.FlashCardRepository;
import com.loopca.app.repository.CardGroupRepository;
import com.loopca.app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardGroupService {
    private final CardGroupRepository cardGroupRepository;
    private final FlashCardRepository flashCardRepository;
    private final MemberRepository memberRepository;

    // 카드 그룹 조회
    public List<CardGroupResponse> getGroupsByMemberIdx(Long memberIdx) {
        return cardGroupRepository.findByMember_Idx(memberIdx)
                .stream()
                .map(group -> CardGroupResponse.builder()
                        .idx(group.getIdx())
                        .groupName(group.getGroupName())
                        .description(group.getDescription())
                        .updatedAt(group.getUpdatedAt())
                        .build()
                )
                .toList();
    }

    // 카드 그룹 저장
    @Transactional
    public CardGroup createCardGroup(CardGroupRequest request, Long memberIdx) {
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        // 그룹 저장
        CardGroup cardGroup = new CardGroup();
        cardGroup.setGroupName(request.getGroupName());
        cardGroup.setDescription(request.getDescription());
        cardGroup.setMember(member);
        CardGroup savedGroup = cardGroupRepository.save(cardGroup);

        // 카드 리스트 변환
        if (request.getCards() != null && !request.getCards().isEmpty()) {
            List<FlashCard> cards = request.getCards().stream()
                    .map(c -> {
                        FlashCard card = new FlashCard();
                        card.setFrontText(c.getFrontText());
                        card.setBackText(c.getBackText());
                        card.setCardGroup(savedGroup); // FK 연결
                        return card;
                    })
                    .toList();

            flashCardRepository.saveAll(cards); // for문 대신 한 번에 INSERT
        }

        return savedGroup;
    }

}
