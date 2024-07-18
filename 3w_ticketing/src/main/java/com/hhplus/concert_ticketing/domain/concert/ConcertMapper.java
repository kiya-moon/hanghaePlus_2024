package com.hhplus.concert_ticketing.domain.concert;

import com.hhplus.concert_ticketing.presentation.concert.Concert;
import com.hhplus.concert_ticketing.presentation.concert.ConcertOption;
import com.hhplus.concert_ticketing.presentation.concert.Seat;

public class ConcertMapper {
    public static Concert toDTO(ConcertEntity entity) {
        Concert dto = new Concert();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
    public static ConcertOption toDTO(ConcertOptionEntity entity) {
        ConcertOption dto = new ConcertOption();
        dto.setId(entity.getId());
        dto.setConcertId(entity.getConcertId());
        dto.setConcertDate(entity.getConcertDate());
        return dto;
    }

    public static ConcertOptionEntity toEntity(ConcertOption dto) {
        ConcertOptionEntity entity = new ConcertOptionEntity();
        entity.setId(dto.getId());
        entity.setConcertId(dto.getConcertId());
        entity.setConcertDate(dto.getConcertDate());
        return entity;
    }

    public static Seat toDTO(SeatEntity entity) {
        Seat dto = new Seat();
        dto.setId(entity.getId());
        dto.setConcertOptionId(entity.getConcertOptionId());
        dto.setSeatNumber(entity.getSeatNumber());
        dto.setStatus(entity.getStatus().toString());
        return dto;
    }

    public static SeatEntity toEntity(Seat dto) {
        SeatEntity entity = new SeatEntity();
        entity.setId(dto.getId());
        entity.setConcertOptionId(dto.getConcertOptionId());
        entity.setSeatNumber(dto.getSeatNumber());
        entity.setStatus(SeatStatus.valueOf(dto.getStatus()));
        return entity;
    }

}
