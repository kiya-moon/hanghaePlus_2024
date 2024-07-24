package com.hhplus.concert_ticketing.presentation.concert;

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
    private List<SeatDto> seatDtoList;

}
