package com.example.kkopja.repository;

import com.example.kkopja.entity.Cafe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CafeRepository extends JpaRepository<Cafe, Integer> {

    Optional<Cafe> findByKakaoId(String kakaoId);

    Page<Cafe> findByNameContaining(String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM cafes c " +
            "WHERE ST_Distance_Sphere(POINT(c.longitude, c.latitude), POINT(:lon, :lat)) <= (:radius * 1000) " +
            "ORDER BY ST_Distance_Sphere(POINT(c.longitude, c.latitude), POINT(:lon, :lat)) ASC",
            nativeQuery = true)
    List<Cafe> findNearbyCafes(@Param("lat") double lat,
                               @Param("lon") double lon,
                               @Param("radius") double radius);

    boolean existsByName(String name);
}