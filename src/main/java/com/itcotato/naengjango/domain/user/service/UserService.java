package com.itcotato.naengjango.domain.user.service;

import com.itcotato.naengjango.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    /**
     * 회원가입 시 아이디 중복 확인
     * 중복이면 true, 사용 가능하면 false 반환
     */

    public boolean isLoginIdDuplicate(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }
}
