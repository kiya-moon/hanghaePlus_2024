package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.queue.QueueService;
import com.hhplus.concert_ticketing.interfaces.api.queue.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueFacade {

    private static final Logger logger = LoggerFactory.getLogger(QueueFacade.class);
    private final QueueService queueService;

    // 토큰 생성 및 대기열에 추가
    public String requestToken(Long userId) {
        String token = java.util.UUID.randomUUID().toString();  // 토큰 생성
        queueService.addTokenToWaitingList(token);
        logger.info("사용자 ID {}에 대한 토큰 발급 완료: {}", userId, token);
        return token;
    }

    // 토큰 상태 확인
    public TokenResponse checkTokenStatus(String token) {
        if (queueService.checkTokenValidity(token)) {
            // 토큰이 활성화 상태일 경우
            return new TokenResponse("200", "예약 페이지로 이동합니다.", token, 0);
        } else {
            // 토큰이 대기열에 있을 경우
            long tokenRank = queueService.getTokenRank(token);
            if (tokenRank == -1) {
                // 대기열에 없는 토큰
                return new TokenResponse("400", "토큰이 유효하지 않습니다.", token, 0);
            }
            long waitingCount = queueService.getWaitingCount();
            return new TokenResponse("100", "계속 대기합니다.", token, (int) (waitingCount - tokenRank));
        }
    }
}
