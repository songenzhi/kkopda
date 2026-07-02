package com.example.kkopja.repository;

import com.example.kkopja.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByCafeId(Integer cafeId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.cafe.id = :cafeId")
    Double getAverageRating(Integer cafeId);
}
