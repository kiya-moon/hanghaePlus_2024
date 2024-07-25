package com.hhplus.concert_ticketing.domain.user;

import com.hhplus.concert_ticketing.domain.concert.SeatEntity;
import com.hhplus.concert_ticketing.domain.concert.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double balance;

    @Version
    private int version;  // 낙관적 락을 위한 버전 필드 추가

    public UserEntity(Long id, Double balance) {
    }

    public static UserEntity createUSER(Long id, Double balance) {
        return new UserEntity(id, balance);
    }

    public void decreaseBalance(Double price) {
        if (balance < price) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }
        balance -= price;
    }
}

