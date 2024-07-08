package com.hhplus.concert_ticketing.presentation.dto.response;

import com.hhplus.concert_ticketing.presentation.dto.Seat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSeatsResponse {
    private List<Seat> seats;
}
