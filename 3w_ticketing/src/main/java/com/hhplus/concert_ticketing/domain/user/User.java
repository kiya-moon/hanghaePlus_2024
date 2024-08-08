package com.hhplus.concert_ticketing.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int balance;

    @Version
    private int version;  // 낙관적 락을 위한 버전 필드 추가

    public User(Long id, int balance) {
        this.id = id;
        this.balance = balance;
    }

    public static User createUser(Long id, int balance) {
        return new User(id, balance);
    }

    public void decreaseBalance(int price) {
        if (balance < price) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }
        balance -= price;
    }
}

