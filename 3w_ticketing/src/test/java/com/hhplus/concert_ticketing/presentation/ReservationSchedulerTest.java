package com.hhplus.concert_ticketing.presentation;

import com.hhplus.concert_ticketing.application.ReservationFacade;
import com.hhplus.concert_ticketing.presentation.reservation.ReservationScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class ReservationSchedulerTest {

    @Mock
    private ReservationFacade reservationFacade;

    @InjectMocks
    private ReservationScheduler reservationScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void manageReservationStatus_테스트() {
        // when
        reservationScheduler.manageReservationStatus();

        // then
        verify(reservationFacade, times(1)).manageReservationStatus();
    }
}
