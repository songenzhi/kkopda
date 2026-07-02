package com.example.kkopja.repository;

import com.example.kkopja.entity.Cafe;
import com.example.kkopja.entity.CongestionVote;
import com.example.kkopja.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CongestionVoteRepository extends JpaRepository<CongestionVote, Long> {

    // 🎯 cafeId, userId 대신 cafe, user 객체를 조건으로 조회합니다.
    Optional<CongestionVote> findByCafeAndUserAndCreatedAtAfter(Cafe cafe, User user, LocalDateTime dateTime);

    List<CongestionVote> findByCafeAndCreatedAtAfter(Cafe cafe, LocalDateTime oneHourAgo);

}