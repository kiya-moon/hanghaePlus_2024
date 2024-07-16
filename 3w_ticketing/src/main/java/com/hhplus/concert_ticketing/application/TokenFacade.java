package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.queue.QueueService;
import com.hhplus.concert_ticketing.domain.queue.TokenStatus;
import com.hhplus.concert_ticketing.domain.user.UserEntity;
import com.hhplus.concert_ticketing.domain.user.UserRepository;
import com.hhplus.concert_ticketing.presentation.queue.TokenResponse;
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
        TokenResponse tokenResponse = queueService.checkToken(token).getBody();
        if (tokenResponse != null) {
            switch (tokenResponse.getResult()) {
                case "100":
                    return TokenStatus.WAITING;
                case "200":
                    return TokenStatus.ACTIVE;
                default:
                    throw new RuntimeException("잘못된 토큰 상태");
            }
        }
        throw new RuntimeException("토큰 상태 확인 오류");
    }
}
