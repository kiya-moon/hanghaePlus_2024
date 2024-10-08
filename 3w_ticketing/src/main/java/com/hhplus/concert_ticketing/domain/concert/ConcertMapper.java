package com.hhplus.concert_ticketing.domain.concert;

import com.hhplus.concert_ticketing.interfaces.api.concert.ConcertDto;
import com.hhplus.concert_ticketing.interfaces.api.concert.ConcertOptionDto;
import com.hhplus.concert_ticketing.interfaces.api.concert.SeatDto;

public class ConcertMapper {
    public static ConcertDto toDTO(Concert entity) {
        ConcertDto dto = new ConcertDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
    public static ConcertOptionDto toDTO(ConcertOption entity) {
        ConcertOptionDto dto = new ConcertOptionDto();
        dto.setId(entity.getId());
        dto.setConcertId(entity.getConcertId());
        dto.setConcertDate(entity.getConcertDate());
        return dto;
    }

    public static ConcertOption toEntity(ConcertOptionDto dto) {
        ConcertOption entity = new ConcertOption();
        entity.setId(dto.getId());
        entity.setConcertId(dto.getConcertId());
        entity.setConcertDate(dto.getConcertDate());
        return entity;
    }

    public static SeatDto toDTO(Seat entity) {
        SeatDto dto = new SeatDto();
        dto.setId(entity.getId());
        dto.setConcertOptionId(entity.getConcertOptionId());
        dto.setSeatNumber(entity.getSeatNumber());
        dto.setStatus(entity.getStatus().toString());
        return dto;
    }

    public static Seat toEntity(SeatDto dto) {
        Seat entity = new Seat();
        entity.setId(dto.getId());
        entity.setConcertOptionId(dto.getConcertOptionId());
        entity.setSeatNumber(dto.getSeatNumber());
        entity.setStatus(SeatStatus.valueOf(dto.getStatus()));
        return entity;
    }

}
