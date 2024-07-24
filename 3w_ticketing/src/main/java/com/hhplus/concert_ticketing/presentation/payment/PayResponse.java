package com.hhplus.concert_ticketing.presentation.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayResponse {
    private String result;
    private String message;
    private PayDto payDto;
}
