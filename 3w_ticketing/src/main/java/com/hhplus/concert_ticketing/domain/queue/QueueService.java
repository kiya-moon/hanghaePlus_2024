package com.hhplus.concert_ticketing.domain.queue;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class QueueService {

    private final RedisTemplate<String, String> redisTemplate;

    // Redis에서 사용하는 키 값들
    private static final String WAITING_TOKENS_KEY = "waiting_tokens";
    private static final String ACTIVE_TOKENS_KEY = "active_tokens";

    // 분당 활성화할 토큰 수
    private static final int TOKENS_TO_ACTIVATE_PER_MINUTE = 420;

    // 토큰 만료 시간 세팅(5분 = 300초)
    private static final long TOKEN_TTL_SECONDS = 300L;

    public QueueService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 토큰을 대기열에 추가
    public void addTokenToWaitingList(String token) {
        redisTemplate.opsForZSet().add(WAITING_TOKENS_KEY, token, System.currentTimeMillis());
    }

    // 토큰이 활성화된 상태인지 확인하는 메서드
    public boolean checkTokenValidity(String token) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(ACTIVE_TOKENS_KEY, token));   // NullPointException 방지
    }

    // 대기 중인 토큰 수를 반환
    public long getWaitingCount() {
        return Optional.ofNullable(redisTemplate.opsForZSet().size(WAITING_TOKENS_KEY)).orElse(0L);
    }

    // 특정 토큰의 대기열 내 순위를 반환
    public long getTokenRank(String token) {
        Long rank = redisTemplate.opsForZSet().rank(WAITING_TOKENS_KEY, token);
        return rank != null ? rank : -1; // -1은 대기열에 토큰이 없음을 나타냄
    }

    // 매 1분마다 실행되어 `waiting` 토큰을 `active` 토큰으로 전환하는 메서드
    @Scheduled(fixedRate = 60000)
    public void activateWaitingTokens() {
        // 대기열에서 앞에 있는 420개의 토큰을 가져옴
        Set<String> tokensToActivate = redisTemplate.opsForZSet().range(WAITING_TOKENS_KEY, 0, TOKENS_TO_ACTIVATE_PER_MINUTE - 1);

        if (tokensToActivate != null && !tokensToActivate.isEmpty()) {
            // 가져온 토큰들을 활성화 목록으로 이동하고, TTL 설정
            for (String token : tokensToActivate) {
                redisTemplate.opsForSet().add(ACTIVE_TOKENS_KEY, token);
                redisTemplate.expire(ACTIVE_TOKENS_KEY, TOKEN_TTL_SECONDS, TimeUnit.SECONDS);
                redisTemplate.opsForZSet().remove(WAITING_TOKENS_KEY, token);
            }
        }
    }

    // 특정 토큰을 만료 처리
    public void expireToken(String token) {
        // 활성화된 토큰 목록에서 제거
        redisTemplate.opsForSet().remove(ACTIVE_TOKENS_KEY, token);
        // TTL 설정을 제거하여 즉시 만료 처리
        redisTemplate.delete(token);
    }
}
