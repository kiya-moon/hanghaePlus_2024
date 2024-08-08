package com.hhplus.concert_ticketing.domain.concert;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ConcertOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long concertId;
    private Timestamp concertDate;

    public ConcertOption(Long concertId, String date) {
    }
}
