package com.hhplus.concert_ticketing.interfaces.api.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveResponse {
    private String result;
    private String message;
    private ReserveDto reserveDto;
}
