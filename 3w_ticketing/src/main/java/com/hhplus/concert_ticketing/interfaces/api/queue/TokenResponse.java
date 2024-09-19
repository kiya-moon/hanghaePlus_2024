package com.hhplus.concert_ticketing.interfaces.api.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private String result;
    private String message;
    private String token;
    private int position;
}
