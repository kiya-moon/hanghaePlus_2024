package com.hhplus.concert_ticketing.interfaces.api.concert;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConcertDto {
    private Long id;
    private String name;

    public ConcertDto(String name) {
        this.name = name;
    }
}
