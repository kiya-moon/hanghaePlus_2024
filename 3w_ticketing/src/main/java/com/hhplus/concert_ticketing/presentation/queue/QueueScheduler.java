package com.hhplus.concert_ticketing.presentation.queue;

import com.hhplus.concert_ticketing.application.QueueFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueScheduler {
    private final QueueFacade queueFacade;

    // 매 1분마다 실행되는 스케줄러
    @Scheduled(cron = "0 * * * * *")  // 매분 정각에 실행
    public void manageTokenStatus() {
        queueFacade.manageTokens();
    }
}
