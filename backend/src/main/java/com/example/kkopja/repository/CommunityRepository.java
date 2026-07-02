package com.example.kkopja.repository;

import com.example.kkopja.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CommunityRepository extends JpaRepository<Community, Integer> {
    Page<Community> findAllByOrderByIdDesc(Pageable pageable);

    @Modifying
    @Query("DELETE FROM Community c WHERE c.user.id = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
}
