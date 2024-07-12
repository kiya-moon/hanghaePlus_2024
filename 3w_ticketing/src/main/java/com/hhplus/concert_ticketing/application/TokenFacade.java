package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.queue.QueueService;
import com.hhplus.concert_ticketing.domain.queue.TokenEntity;
import com.hhplus.concert_ticketing.domain.queue.TokenStatus;
import com.hhplus.concert_ticketing.domain.user.UserEntity;
import com.hhplus.concert_ticketing.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenFacade {

    private final UserRepository userRepository;
    private final QueueService queueService;

    public String requestToken(Long userId) {
        // 유저 정보 확인
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        // 토큰 생성
        return queueService.generateToken(userId).getToken();
    }

    public TokenStatus checkTokenStatus(String token) {
        return queueService.checkToken(token);
    }
}

