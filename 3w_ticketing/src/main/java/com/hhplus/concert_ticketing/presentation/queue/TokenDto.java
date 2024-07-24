package com.hhplus.concert_ticketing.presentation.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    private String token;
    private int position;
    private String expiresAt;
    private int waitingCount;
}
