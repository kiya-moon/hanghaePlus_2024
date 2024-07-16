package com.hhplus.concert_ticketing.domain.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.hhplus.concert_ticketing.domain.queue.TokenStatus.WAITING;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueueSchedulerTest {

    private QueueScheduler queueScheduler;
    private QueueRepository queueRepository;

    @BeforeEach
    void setUp() {
        queueRepository = Mockito.mock(QueueRepository.class);
        queueScheduler = new QueueScheduler(queueRepository);
    }

    @Test
    void 만료된_토큰_상태_변경_테스트() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        TokenEntity expiredToken = new TokenEntity();
        expiredToken.setExpiresAt(new Timestamp(now.getTime() - 10000));  // 현재 시간보다 이전
        expiredToken.setStatus(WAITING);

        List<TokenEntity> expiredTokens = new ArrayList<>();
        expiredTokens.add(expiredToken);

        when(queueRepository.findByExpiresAtBeforeAndStatus(now, "WAITING")).thenReturn(expiredTokens);

        queueScheduler.manageTokens();

        verify(queueRepository, times(1)).save(expiredToken);
        assertEquals("EXPIRED", expiredToken.getStatus());
    }

    @Test
    void 대기중인_토큰_활성화_테스트() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        TokenEntity waitingToken1 = new TokenEntity();
        waitingToken1.setStatus(WAITING);

        TokenEntity waitingToken2 = new TokenEntity();
        waitingToken2.setStatus(WAITING);

        List<TokenEntity> waitingTokens = new ArrayList<>();
        waitingTokens.add(waitingToken1);
        waitingTokens.add(waitingToken2);

        when(queueRepository.findByStatus("WAITING")).thenReturn(waitingTokens);
        when(queueRepository.countByStatus("ACTIVE")).thenReturn(29L);  // 현재 ACTIVATE 상태의 토큰이 29개

        queueScheduler.manageTokens();

        verify(queueRepository, times(2)).save(any(TokenEntity.class));  // 두 개의 토큰이 저장되어야 함

        assertEquals("ACTIVATE", waitingToken1.getStatus());
        assertEquals("ACTIVATE", waitingToken2.getStatus());
    }
}
