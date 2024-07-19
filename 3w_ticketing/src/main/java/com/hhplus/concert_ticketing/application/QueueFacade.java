package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.queue.*;
import com.hhplus.concert_ticketing.domain.user.UserEntity;
import com.hhplus.concert_ticketing.domain.user.UserService;
import com.hhplus.concert_ticketing.presentation.queue.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class QueueFacade {

    private static final Logger logger = LoggerFactory.getLogger(QueueFacade.class);

    private final QueueService queueService;
    private final UserService userService;
    private final QueueRepository queueRepository;

    // 토큰 생성
    public String requestToken(Long userId) {
        logger.info("사용자 ID {}에 대한 토큰 생성 요청", userId);

        // 사용자 조회
        UserEntity user;
        try {
            user = userService.getUserInfo(userId);
            logger.info("사용자 ID {} 조회 성공", userId);
        } catch (Exception e) {
            logger.error("사용자 ID {} 조회 실패: {}", userId, e.getMessage());
            throw e;
        }

        // 토큰 생성
        String token;
        try {
            token = queueService.generateToken(userId).getToken();
            logger.info("사용자 ID {}에 대한 토큰 생성 성공: {}", userId, token);
        } catch (Exception e) {
            logger.error("사용자 ID {}에 대한 토큰 생성 실패: {}", userId, e.getMessage());
            throw e;
        }

        return token;
    }

    // 토큰 상태 확인
    public TokenResponse checkTokenStatus(String token) {
        logger.info("토큰 상태 확인 요청: {}", token);

        TokenEntity tokenEntity;
        try {
            tokenEntity = queueService.checkToken(token);
            logger.info("토큰 조회 성공: {}", token);
        } catch (Exception e) {
            logger.error("토큰 조회 실패: {}", token, e.getMessage());
            throw e;
        }

        TokenStatus status = tokenEntity.getStatus();
        logger.info("토큰 상태: {}", status);

        if (status == TokenStatus.ACTIVE) {
            logger.info("토큰 {} 상태: 활성화", token);
            return QueueMapper.toResponseDTO(tokenEntity, "200", "예약 페이지로 이동합니다.", 0);
        } else {
            int waitingCount = queueService.getWaitingCount(token) + 30;    // 최대 active 인원은 30명
            logger.info("토큰 {} 상태: 대기 중, 대기 인원: {}", token, waitingCount);
            return QueueMapper.toResponseDTO(tokenEntity, "100", "계속 대기합니다.", waitingCount);
        }
    }

    // 토큰 상태 관리
    public void manageTokens() {
        logger.info("토큰 상태 관리 시작");
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try {
            // 만료된 토큰 상태를 EXPIRED로 변경
            queueService.expireTokens();
            logger.info("만료된 토큰 상태 변경 완료");

            // 활성화 토큰이 30개 미만이면, 대기 중인 토큰을 활성화로 변경
            queueService.activateTokens();
            logger.info("대기 중인 토큰 활성화 완료");
        } catch (Exception e) {
            logger.error("토큰 상태 관리 실패: {}", e.getMessage());
            throw e;
        }
    }
}
