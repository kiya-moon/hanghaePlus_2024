package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.queue.*;
import com.hhplus.concert_ticketing.domain.user.UserEntity;
import com.hhplus.concert_ticketing.domain.user.UserRepository;
import com.hhplus.concert_ticketing.domain.user.UserService;
import com.hhplus.concert_ticketing.presentation.queue.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static com.hhplus.concert_ticketing.domain.queue.TokenStatus.ACTIVE;
import static com.hhplus.concert_ticketing.domain.queue.TokenStatus.EXPIRED;

@Service
@RequiredArgsConstructor
public class QueueFacade {

    private final QueueService queueService;
    private final UserService userService;

    private final QueueRepository queueRepository;

    // 토큰 생성
    public String requestToken(Long userId) {

        // 유저 조회 > 유효x
        // 유저 조회 > 토큰 생성

        // 사용자 조회
        UserEntity user = userService.getUserInfo(userId);

        // 토큰 생성
        return queueService.generateToken(userId).getToken();
    }

    // 토큰 상태 확인
    public TokenResponse checkTokenStatus(String token) {
        // 토큰 조회 > status에 맞게 응답

        TokenEntity tokenEntity = queueService.checkToken(token);
        TokenStatus status = tokenEntity.getStatus();

        if (status == TokenStatus.ACTIVE) {
            return QueueMapper.toResponseDTO(tokenEntity, "200", "예약 페이지로 이동합니다.", 0);
        } else {
            int waitingCount = queueService.getWaitingCount(token) + 30;    // 최대 active 인원은 30명
            return QueueMapper.toResponseDTO(tokenEntity, "100", "계속 대기합니다.", waitingCount);
        }
    }

    // 토큰 상태 관리
    public void manageTokens() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // 만료된 토큰 상태를 EXPIRED로 변경
        queueService.expireTokens();

        // 활성화 토큰이 30개 미만이면, 대기 중인 토큰을 활성화로 변경
        queueService.activateTokens();
    }
}
