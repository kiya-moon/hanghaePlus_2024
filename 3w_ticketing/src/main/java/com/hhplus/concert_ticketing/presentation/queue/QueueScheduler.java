package com.hhplus.concert_ticketing.presentation.queue;

import com.hhplus.concert_ticketing.application.QueueFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // SLF4J 로깅 라이브러리
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueScheduler {
    private final QueueFacade queueFacade;

    // 매 1분마다 실행되는 스케줄러
    @Scheduled(cron = "0 * * * * *")  // 매분 정각에 실행
    public void manageTokenStatus() {
        log.info("토큰 상태 관리 작업을 {}에 시작합니다.", System.currentTimeMillis());
        try {
            queueFacade.manageTokens();
            log.info("토큰 상태 관리 작업이 {}에 성공적으로 완료되었습니다.", System.currentTimeMillis());
        } catch (Exception e) {
            log.error("토큰 상태 관리 작업 중 오류가 발생했습니다.", e);
        }
    }
}
