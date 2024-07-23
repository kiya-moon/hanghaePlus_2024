package com.hhplus.concert_ticketing.domain.concert;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ConcertOptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long concertId;
    private Timestamp concertDate;

    public ConcertOptionEntity(long l, Long concertId, String date, double v) {
    }
}
