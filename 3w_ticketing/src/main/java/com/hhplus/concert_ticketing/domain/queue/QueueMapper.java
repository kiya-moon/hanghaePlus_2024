package com.hhplus.concert_ticketing.domain.queue;

import com.hhplus.concert_ticketing.presentation.ErrorResponse;
import com.hhplus.concert_ticketing.presentation.queue.TokenDto;
import com.hhplus.concert_ticketing.presentation.queue.TokenResponse;

public class QueueMapper {
    public static TokenDto toDTO(TokenEntity entity, int waitingCount) {
        return new TokenDto(entity.getToken(), 0, entity.getExpiresAt().toString(), waitingCount);
    }

    public static TokenResponse toResponseDTO(TokenEntity entity, String result, String message, int waitingCount) {
        return new TokenResponse(result, message, toDTO(entity, waitingCount));
    }

    public static ErrorResponse toErrorResponseDTO(String result, String message) {
        return new ErrorResponse(result, message);
    }
}
