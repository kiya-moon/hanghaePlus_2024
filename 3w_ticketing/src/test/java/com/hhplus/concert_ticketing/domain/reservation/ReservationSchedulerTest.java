package com.hhplus.concert_ticketing.domain.reservation;

import com.hhplus.concert_ticketing.domain.concert.SeatEntity;
import com.hhplus.concert_ticketing.domain.concert.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.hhplus.concert_ticketing.domain.concert.SeatStatus.LOCKED;
import static com.hhplus.concert_ticketing.domain.concert.SeatStatus.UNLOCKED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationSchedulerTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private ReservationScheduler reservationScheduler;

    @Test
    public void testManageReservations() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Mocking expired and non-expired reservations
        ReservationEntity expiredReservation1 = new ReservationEntity();
        expiredReservation1.setId(1L);
        expiredReservation1.setExpiredAt(new Timestamp(now.getTime() - 1000));
        expiredReservation1.setStatus("ACTIVE");
        expiredReservation1.setSeatId(1L);

        ReservationEntity expiredReservation2 = new ReservationEntity();
        expiredReservation2.setId(2L);
        expiredReservation2.setExpiredAt(new Timestamp(now.getTime() - 2000));
        expiredReservation2.setStatus("ACTIVE");
        expiredReservation2.setSeatId(2L);

        ReservationEntity activeReservation = new ReservationEntity();
        activeReservation.setId(3L);
        activeReservation.setExpiredAt(new Timestamp(now.getTime() + 1000));
        activeReservation.setStatus("ACTIVE");
        activeReservation.setSeatId(3L);

        List<ReservationEntity> expiredReservations = Arrays.asList(expiredReservation1, expiredReservation2);

        // Mocking seat entities
        SeatEntity seat1 = new SeatEntity();
        seat1.setId(1L);
        seat1.setStatus(LOCKED);

        SeatEntity seat2 = new SeatEntity();
        seat2.setId(2L);
        seat2.setStatus(LOCKED);

        SeatEntity seat3 = new SeatEntity();
        seat3.setId(3L);
        seat3.setStatus(LOCKED);

        // Mocking repository methods
        when(reservationRepository.findByExpiresAtBeforeAndStatus(any(Timestamp.class), eq("ACTIVE"))).thenReturn(expiredReservations);
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat1));
        when(seatRepository.findById(2L)).thenReturn(Optional.of(seat2));
        when(seatRepository.findById(3L)).thenReturn(Optional.of(seat3));

        // Call the method under test
        reservationScheduler.manageReservations();

        // Verify the reservation status was updated for expired reservations
        verify(reservationRepository, times(1)).save(expiredReservation1);
        verify(reservationRepository, times(1)).save(expiredReservation2);
        assert "EXPIRED".equals(expiredReservation1.getStatus());
        assert "EXPIRED".equals(expiredReservation2.getStatus());

        // Verify the seat status was updated for expired reservations
        verify(seatRepository, times(1)).save(seat1);
        verify(seatRepository, times(1)).save(seat2);
        assert UNLOCKED.equals(seat1.getStatus());
        assert UNLOCKED.equals(seat2.getStatus());

        // Verify the non-expired reservation and seat were not updated
        verify(reservationRepository, times(0)).save(activeReservation);
        verify(seatRepository, times(0)).save(seat3);
        assert "ACTIVE".equals(activeReservation.getStatus());
        assert LOCKED.equals(seat3.getStatus());
    }
}
