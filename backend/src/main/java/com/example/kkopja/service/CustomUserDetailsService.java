package com.example.kkopja.service;

import com.example.kkopja.entity.User;
import com.example.kkopja.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { // 파라미터명도 email로 바꾸면 더 명확합니다
        // findByUsername 대신 findByEmail 호출!
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일로 가입된 사용자를 찾을 수 없습니다: " + email));

        return new CustomUserDetails(user);
    }
}