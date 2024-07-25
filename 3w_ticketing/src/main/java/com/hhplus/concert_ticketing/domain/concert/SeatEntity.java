package com.hhplus.concert_ticketing.domain.concert;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import static com.hhplus.concert_ticketing.domain.concert.SeatStatus.LOCKED;
import static com.hhplus.concert_ticketing.domain.concert.SeatStatus.UNLOCKED;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long concertOptionId;
    private String seatNumber;
    private SeatStatus status;
    private Double price;

    public SeatEntity(long l, Long concertOptionId, String number, SeatStatus seatStatus) {
    }

    public static SeatEntity createSeat(Long id, Long concertOptionId, String seatNumber, SeatStatus status, Double price) {
        return new SeatEntity(id, concertOptionId, seatNumber, status, price);
    }

    public void unlockSeat() {
        this.setStatus(UNLOCKED);
    }
    public void lockSeat() {
        this.setStatus(LOCKED);
    }
}
