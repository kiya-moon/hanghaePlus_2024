package com.hhplus.concert_ticketing.domain.concert;

import com.hhplus.concert_ticketing.presentation.concert.ConcertDto;
import com.hhplus.concert_ticketing.presentation.concert.ConcertOptionDto;
import com.hhplus.concert_ticketing.presentation.concert.SeatDto;

public class ConcertMapper {
    public static ConcertDto toDTO(ConcertEntity entity) {
        ConcertDto dto = new ConcertDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
    public static ConcertOptionDto toDTO(ConcertOptionEntity entity) {
        ConcertOptionDto dto = new ConcertOptionDto();
        dto.setId(entity.getId());
        dto.setConcertId(entity.getConcertId());
        dto.setConcertDate(entity.getConcertDate());
        return dto;
    }

    public static ConcertOptionEntity toEntity(ConcertOptionDto dto) {
        ConcertOptionEntity entity = new ConcertOptionEntity();
        entity.setId(dto.getId());
        entity.setConcertId(dto.getConcertId());
        entity.setConcertDate(dto.getConcertDate());
        return entity;
    }

    public static SeatDto toDTO(SeatEntity entity) {
        SeatDto dto = new SeatDto();
        dto.setId(entity.getId());
        dto.setConcertOptionId(entity.getConcertOptionId());
        dto.setSeatNumber(entity.getSeatNumber());
        dto.setStatus(entity.getStatus().toString());
        return dto;
    }

    public static SeatEntity toEntity(SeatDto dto) {
        SeatEntity entity = new SeatEntity();
        entity.setId(dto.getId());
        entity.setConcertOptionId(dto.getConcertOptionId());
        entity.setSeatNumber(dto.getSeatNumber());
        entity.setStatus(SeatStatus.valueOf(dto.getStatus()));
        return entity;
    }

}
