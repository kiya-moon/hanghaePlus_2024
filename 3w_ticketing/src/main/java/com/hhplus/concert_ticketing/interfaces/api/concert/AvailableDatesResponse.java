package com.hhplus.concert_ticketing.interfaces.api.concert;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableDatesResponse {
    private List<ConcertOptionDto> concertOptionDtoList;
}
