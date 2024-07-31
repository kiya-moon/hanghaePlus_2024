package com.hhplus.concert_ticketing.domain.reservation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.sql.Timestamp;

import static com.hhplus.concert_ticketing.domain.reservation.ReservationStatus.COMPLETE;
import static com.hhplus.concert_ticketing.domain.reservation.ReservationStatus.EXPIRED;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long seatId;
    private String status;
    private Timestamp createdAt;
    private Timestamp expiresAt;
    private int price;

    public ReservationEntity(Long userId, Long seatId, Timestamp createdAt, Timestamp expiresAt, int price) {
        this.userId = userId;
        this.seatId = seatId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.price = price;
    }

    public static ReservationEntity createReservation(Long userId, Long seatId, Timestamp createdAt, Timestamp expiresAt, int price) {
        return new ReservationEntity(userId, seatId, createdAt, expiresAt, price);
    }

    public void expireReservation() {
        this.setStatus(EXPIRED.toString());
    }

    public void completeReservation() {
        this.setStatus(COMPLETE.toString());
    }
}
