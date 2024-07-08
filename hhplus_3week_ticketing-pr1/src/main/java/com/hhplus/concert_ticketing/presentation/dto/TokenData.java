package com.hhplus.concert_ticketing.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenData {
    private String token;
    private Integer queuePosition;
    private String expiresAt;
}
