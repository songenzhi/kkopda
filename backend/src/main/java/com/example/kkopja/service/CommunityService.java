package com.example.kkopja.service;

import com.example.kkopja.dto.CommunityCreateDto;
import com.example.kkopja.dto.CommunityUpdateDto;
import com.example.kkopja.entity.Community;
import com.example.kkopja.entity.User;
import com.example.kkopja.repository.CommunityRepository;
import com.example.kkopja.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    private final String uploadDir = "E:/projects/uploads/";

    // 커뮤니티 리스트 조회
    public Page<Community> findPage(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return communityRepository.findAllByOrderByIdDesc(pageable);
    }

    // 커뮤니티 리스트 단일 조회
    public Community getCommunity(Integer id) {
        return communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 정보를 찾을 수 없습니다. ID: " + id));
    }

    // 커뮤니티 리스트 작성
    public Community saveCommunity(CommunityCreateDto dto, Integer userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        String savedImageUrl = null;
        MultipartFile imageFile = dto.getImageFile();

        // 1. 첨부된 파일이 있는지 확인
        if (imageFile != null && !imageFile.isEmpty()) {
            // 2. 파일명 중복 방지를 위한 UUID 결합
            String originalFilename = imageFile.getOriginalFilename();

            // 1. 파일 이름에서 마지막 '.' 위치를 찾아서 확장자만 추출 (예: ".png")
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 2. UUID(영어+숫자)와 확장자를 결합해서 완벽하게 안전한 파일명 생성
            String uuid = UUID.randomUUID().toString();
            String savedFilename = uuid + extension;

            // 3. 내 컴퓨터 지정된 폴더에 물리적 파일 저장
            File dest = new File(uploadDir + savedFilename);
            imageFile.transferTo(dest);

            // 4. DB에 저장할 웹 접근 주소(URL 경로) 설정
            savedImageUrl = "/uploads/" + savedFilename;
        }

        // 5. DTO의 데이터와 이미지 URL을 조합하여 엔티티 생성
        Community community = Community.builder()
                .user(user)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .imageUrl(savedImageUrl) // DB 컬럼에 사진 경로 저장
                .build();

        return communityRepository.save(community);
    }

    // 커뮤니티 리스트 수정
    public Community updateCommunity(Integer id, CommunityUpdateDto dto, Integer loginUserId) throws IOException {
        Community existingCommunity = communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 정보를 수정할 수 없습니다."));

        if (!existingCommunity.getUser().getId().equals(loginUserId)) {
            throw new RuntimeException("남의 글은 수정할 수 없습니다!");
        }

        existingCommunity.setTitle(dto.getTitle());
        existingCommunity.setDescription(dto.getDescription());

        MultipartFile newImageFile = dto.getImageFile();

        // 상황 A: 사용자가 기존 이미지를 삭제했거나, 아예 새로운 이미지를 업로드한 경우 -> 기존 파일 삭제 필요
        if (dto.isImageDeleted() || (newImageFile != null && !newImageFile.isEmpty())) {

            // 기존에 저장된 사진 주소가 있었다면 실제 서버 폴더에서 파일 삭제
            if (existingCommunity.getImageUrl() != null) {
                // "/uploads/uuid_이름.jpg" -> "C:/kkopja/uploads/uuid_이름.jpg" 로 경로 변환
                String oldFilePath = uploadDir + existingCommunity.getImageUrl().replace("/uploads/", "");
                File oldFile = new File(oldFilePath);
                if (oldFile.exists()) {
                    oldFile.delete(); // 💾 서버 폴더에서 이전 사진 삭제!
                }
            }

            // 일단 이미지 경로를 null로 비워둠 (삭제 요청이었을 경우 이 상태로 유지됨)
            existingCommunity.setImageUrl(null);
        }

        // 상황 B: 새로운 이미지가 첨부되어 넘어온 경우 -> 새 파일 저장 및 경로 갱신
        if (newImageFile != null && !newImageFile.isEmpty()) {
            String originalFilename = newImageFile.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uuid = UUID.randomUUID().toString();
            String savedFilename = uuid + extension;

            // 새 파일 저장
            File dest = new File(uploadDir + savedFilename);
            newImageFile.transferTo(dest);

            // 새로운 이미지 URL 설정
            existingCommunity.setImageUrl("/uploads/" + savedFilename);
        }

        // 상황 C: 위의 조건들에 걸리지 않았다면(새 파일도 없고, 삭제 체크도 안 됨), 기존의 imageUrl이 그대로 유지돼!

        return communityRepository.save(existingCommunity);
    }

    // 커뮤니티 리스트 삭제
    public void deleteCommunity(Integer id, Integer loginUserId) {
        Community existingCommunity = communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 글을 찾을 수 없습니다."));

        // 🚨 방어막: 지우기 전에도 본인이 맞는지 꼭 확인!
        if (!existingCommunity.getUser().getId().equals(loginUserId)) {
            throw new RuntimeException("남의 글은 삭제할 수 없습니다!");
        }

        communityRepository.delete(existingCommunity);
    }
}
