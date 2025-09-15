package com.loopca.app.repository;

import com.loopca.app.entity.CardGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardGroupRepository extends JpaRepository<CardGroup, Long> {
    List<CardGroup> findByMember_Idx(Long memberIdx);
}