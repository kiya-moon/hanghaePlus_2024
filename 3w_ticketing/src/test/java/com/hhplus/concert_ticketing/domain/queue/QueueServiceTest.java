package com.hhplus.concert_ticketing.domain.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QueueServiceTest {

    private QueueService queueService;
    private QueueRepository queueRepository;

    @BeforeEach
    void setUp() {
        queueRepository = Mockito.mock(QueueRepository.class);
        queueService = new QueueService(queueRepository);
    }

    @Test
    void 유저_ID로_토큰_발급_여부_확인_테스트() {
        Long userId = 1L;

        // 유저 ID로 발급된 토큰이 없는 경우
        when(queueRepository.existsByUserId(userId)).thenReturn(true);
        assertTrue(queueService.isTokenIssued(userId));
    }

    @Test
    void 유저_ID로_토큰_생성_테스트() {
        Long userId = 1L;
        UUID tokenUUID = UUID.randomUUID();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp expiresAt = new Timestamp(now.getTime() + 5 * 60 * 1000);  // 5분 후

        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken(tokenUUID.toString());
        tokenEntity.setUserId(userId);
        tokenEntity.setStatus("WAITING");
        tokenEntity.setCreatedAt(now);
        tokenEntity.setExpiresAt(expiresAt);

        when(queueRepository.existsByUserId(userId)).thenReturn(false);
        when(queueRepository.save(Mockito.any(TokenEntity.class))).thenReturn(tokenEntity);

        TokenEntity createdToken = queueService.generateToken(userId);

        assertEquals(userId, createdToken.getUserId());
        assertNotNull(createdToken.getToken());
        assertEquals("WAITING", createdToken.getStatus());
    }

    @Test
    void 토큰_조회_상태에_따른_반환_테스트() {
        String waitingToken = "waiting-token";
        TokenEntity waitingTokenEntity = new TokenEntity();
        waitingTokenEntity.setToken(waitingToken);
        waitingTokenEntity.setStatus("WAITING");

        when(queueRepository.findByToken(waitingToken)).thenReturn(Optional.of(waitingTokenEntity));
        ResponseEntity<String> responseWaiting = queueService.checkToken(waitingToken);
        assertEquals(HttpStatus.CONTINUE, responseWaiting.getStatusCode());
        assertEquals("100", responseWaiting.getBody());

        String activateToken = "activate-token";
        TokenEntity activateTokenEntity = new TokenEntity();
        activateTokenEntity.setToken(activateToken);
        activateTokenEntity.setStatus("ACTIVATE");

        when(queueRepository.findByToken(activateToken)).thenReturn(Optional.of(activateTokenEntity));
        ResponseEntity<String> responseActivate = queueService.checkToken(activateToken);
        assertEquals(HttpStatus.SEE_OTHER, responseActivate.getStatusCode());
        assertEquals("reservation-page", responseActivate.getBody());

        String invalidToken = "invalid-token";
        when(queueRepository.findByToken(invalidToken)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> queueService.checkToken(invalidToken));
        assertEquals("유효하지 않은 접근입니다.", exception.getMessage());
    }


    @Test
    void 토큰_유효성_확인_테스트() {
        String activateToken = "activate-token";
        TokenEntity activateTokenEntity = new TokenEntity();
        activateTokenEntity.setToken(activateToken);
        activateTokenEntity.setStatus("ACTIVATE");

        when(queueRepository.findByToken(activateToken)).thenReturn(Optional.of(activateTokenEntity));
        assertTrue(queueService.checkTokenValidity(activateToken));

        String waitingToken = "waiting-token";
        TokenEntity waitingTokenEntity = new TokenEntity();
        waitingTokenEntity.setToken(waitingToken);
        waitingTokenEntity.setStatus("WAITING");

        when(queueRepository.findByToken(waitingToken)).thenReturn(Optional.of(waitingTokenEntity));
        assertFalse(queueService.checkTokenValidity(waitingToken));

        String invalidToken = "invalid-token";
        when(queueRepository.findByToken(invalidToken)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> queueService.checkTokenValidity(invalidToken));
        assertEquals("유효하지 않은 접근입니다.", exception.getMessage());
    }

    @Test
    void expireToken_정상적으로_만료처리_테스트() {
        // given
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken("valid-token");
        tokenEntity.setStatus("WAITING");

        when(queueRepository.findByToken("valid-token")).thenReturn(java.util.Optional.of(tokenEntity));

        // when
        queueService.expireToken("valid-token");

        // then
        verify(queueRepository, times(1)).save(tokenEntity);
        assertEquals("EXPIRED", tokenEntity.getStatus());
    }
}
