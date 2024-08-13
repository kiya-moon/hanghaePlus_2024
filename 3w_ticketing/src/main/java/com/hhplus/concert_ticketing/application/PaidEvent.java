package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.reservation.Reservation;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaidEvent extends ApplicationEvent {
    private final Reservation reservation;

    public PaidEvent(Object source, Reservation reservation) {  // source : 이벤트 발생 출처
        super(source);
        this.reservation = reservation;
    }

}
