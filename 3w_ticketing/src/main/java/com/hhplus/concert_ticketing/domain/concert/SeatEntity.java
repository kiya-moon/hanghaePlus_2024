package com.hhplus.concert_ticketing.domain.concert;

import jakarta.persistence.*;
import lombok.*;

import static com.hhplus.concert_ticketing.domain.concert.SeatStatus.LOCKED;
import static com.hhplus.concert_ticketing.domain.concert.SeatStatus.UNLOCKED;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long concertOptionId;
    private String seatNumber;
    private SeatStatus status = UNLOCKED;
    private int price;

    @Version
    private Long version;

    public SeatEntity(Long concertOptionId, String number, SeatStatus seatStatus, int price) {
    }

    public void unlockSeat() {
        this.setStatus(UNLOCKED);
    }
    public void lockSeat() {
        this.setStatus(LOCKED);
    }
}
