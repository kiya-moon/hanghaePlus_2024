package com.hhplus.concert_ticketing.presentation.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenData {
    private String token;
    private int position;
    private String expiresAt;
    private int waitingCount;
}
