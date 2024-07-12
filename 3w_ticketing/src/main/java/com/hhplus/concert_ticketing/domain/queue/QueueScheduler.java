package com.hhplus.concert_ticketing.domain.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
@RequiredArgsConstructor
public class QueueScheduler {
    private final QueueRepository queueRepository;

    // 매 1분마다 실행되는 스케줄러
    @Scheduled(cron = "0 * * * * *")  // 매분 정각에 실행
    public void manageTokens() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // 만료된 토큰 상태를 EXPIRED로 변경
        List<TokenEntity> expiredTokens = queueRepository.findByExpiresAtBeforeAndStatus(now, "WAITING");
        for (TokenEntity token : expiredTokens) {
            token.setStatus("EXPIRED");
            queueRepository.save(token);
        }

        // 활성화 토큰이 30개 미만이면, 대기 중인 토큰을 활성화로 변경
        long activeTokenCount = queueRepository.countByStatus("ACTIVATE");
        if (activeTokenCount < 30) {
            List<TokenEntity> waitingTokens = queueRepository.findByStatus("WAITING");
            int tokensToActivate = 30 - (int) activeTokenCount;

            for (int i = 0; i < tokensToActivate && i < waitingTokens.size(); i++) {
                TokenEntity token = waitingTokens.get(i);
                token.setStatus("ACTIVATE");
                queueRepository.save(token);
            }
        }
    }
}
