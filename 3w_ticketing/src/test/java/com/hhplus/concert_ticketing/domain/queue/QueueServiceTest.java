package com.hhplus.concert_ticketing.domain.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.hhplus.concert_ticketing.domain.queue.TokenStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QueueServiceTest {

    @Mock
    private QueueRepository queueRepository;

    @InjectMocks
    private QueueService queueService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 유저_ID로_토큰_발급_여부_확인_테스트() {
        Long userId = 1L;

        // 유저 ID로 발급된 토큰이 있는 경우
        when(queueRepository.existsByUserId(userId)).thenReturn(true);
        assertTrue(queueService.isTokenIssued(userId));

        // 유저 ID로 발급된 토큰이 없는 경우
        when(queueRepository.existsByUserId(userId)).thenReturn(false);
        assertFalse(queueService.isTokenIssued(userId));
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
        tokenEntity.setStatus(WAITING);
        tokenEntity.setCreatedAt(now);
        tokenEntity.setExpiresAt(expiresAt);

        when(queueRepository.existsByUserId(userId)).thenReturn(false);
        when(queueRepository.save(any(TokenEntity.class))).thenReturn(tokenEntity);

        TokenEntity createdToken = queueService.generateToken(userId);

        assertEquals(userId, createdToken.getUserId());
        assertNotNull(createdToken.getToken());
        assertEquals(WAITING, createdToken.getStatus());
    }

    @Test
    void 토큰_조회_상태에_따른_반환_테스트() {
        String waitingToken = "waiting-token";
        TokenEntity waitingTokenEntity = new TokenEntity();
        waitingTokenEntity.setToken(waitingToken);
        waitingTokenEntity.setStatus(WAITING);
        waitingTokenEntity.setExpiresAt(new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000));  // 5분 후

        when(queueRepository.findByToken(waitingToken)).thenReturn(Optional.of(waitingTokenEntity));
        boolean isValidWaitingToken = queueService.checkTokenValidity(waitingToken);
        assertFalse(isValidWaitingToken);

        String activateToken = "activate-token";
        TokenEntity activateTokenEntity = new TokenEntity();
        activateTokenEntity.setToken(activateToken);
        activateTokenEntity.setStatus(ACTIVE);
        activateTokenEntity.setExpiresAt(new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000));  // 5분 후

        when(queueRepository.findByToken(activateToken)).thenReturn(Optional.of(activateTokenEntity));
        boolean isValidActivateToken = queueService.checkTokenValidity(activateToken);
        assertTrue(isValidActivateToken);

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
        tokenEntity.setStatus(WAITING);

        when(queueRepository.findByToken("valid-token")).thenReturn(Optional.of(tokenEntity));

        // when
        queueService.expireToken("valid-token");

        // then
        verify(queueRepository, times(1)).save(tokenEntity);
        assertEquals(EXPIRED, tokenEntity.getStatus());
    }

    @Test
    void expireTokens_정상적으로_만료처리_테스트() {
        // given
        Timestamp now = new Timestamp(System.currentTimeMillis());
        TokenEntity expiredToken1 = new TokenEntity();
        expiredToken1.setToken("expired-token1");
        expiredToken1.setExpiresAt(new Timestamp(now.getTime() - 10000));  // 만료된 토큰 (현재 시간보다 오래됨)

        TokenEntity expiredToken2 = new TokenEntity();
        expiredToken2.setToken("expired-token2");
        expiredToken2.setExpiresAt(new Timestamp(now.getTime() - 10000));  // 만료된 토큰 (현재 시간보다 오래됨)

        List<TokenEntity> expiredTokens = List.of(expiredToken1, expiredToken2);

        // `TokenStatus`와 `Timestamp`를 파라미터로 사용하는 메소드 호출에 맞춰서 `when` 설정
        when(queueRepository.findTokensToExpire(any(TokenStatus.class), any(Timestamp.class)))
                .thenReturn(expiredTokens);

        // when
        queueService.expireTokens();

        // then
        for (TokenEntity token : expiredTokens) {
            assertEquals(TokenStatus.EXPIRED, token.getStatus());
        }

        // Verify that save was called once for each token
        verify(queueRepository, times(expiredTokens.size())).save(any(TokenEntity.class));
    }


    @Test
    void activateTokens_정상적으로_활성화처리_테스트() {
        // given
        TokenEntity waitingToken1 = new TokenEntity();
        waitingToken1.setToken("waiting-token1");
        waitingToken1.setStatus(WAITING);

        TokenEntity waitingToken2 = new TokenEntity();
        waitingToken2.setToken("waiting-token2");
        waitingToken2.setStatus(WAITING);

        List<TokenEntity> waitingTokens = List.of(waitingToken1, waitingToken2);

        when(queueRepository.countByStatus(ACTIVE)).thenReturn(25L);  // 현재 활성화된 토큰이 25개
        when(queueRepository.findTokensToActivate(any(Pageable.class), WAITING)).thenReturn(waitingTokens);

        // when
        queueService.activateTokens();

        // then
        for (TokenEntity token : waitingTokens) {
            assertEquals(ACTIVE, token.getStatus());
        }
        verify(queueRepository, times(waitingTokens.size())).save(any(TokenEntity.class));
    }
}
