package com.example.kkopja.dto;

import com.example.kkopja.entity.Community;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommunityResponseDto {
    private Integer id;
    private String title;
    private String description;
    private String imageUrl; //
    private Integer userId;
    private String userNickname; // 💡 게시판 목록에 '작성자 닉네임'을 띄우기 위해 추가!
    private LocalDateTime createdAt;

    // 엔티티를 DTO 상자로 변환해주는 생성자
    public CommunityResponseDto(Community community) {
        this.id = community.getId();
        this.title = community.getTitle();
        this.description = community.getDescription();
        this.createdAt = community.getCreatedAt();
        this.imageUrl = community.getImageUrl();

        // 💡 작성자(User) 정보가 존재할 때만 안전하게 꺼내오는 방어 코드
        if (community.getUser() != null) {
            this.userId = community.getUser().getId();
            this.userNickname = community.getUser().getNickname();
        }
    }
}