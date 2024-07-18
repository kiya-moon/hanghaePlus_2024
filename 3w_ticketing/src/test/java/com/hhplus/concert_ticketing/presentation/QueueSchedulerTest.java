package com.hhplus.concert_ticketing.presentation;

import com.hhplus.concert_ticketing.application.QueueFacade;
import com.hhplus.concert_ticketing.presentation.queue.QueueScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class QueueSchedulerTest {

    @Mock
    private QueueFacade queueFacade;

    @InjectMocks
    private QueueScheduler queueScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void manageTokenStatus_테스트() {
        // when
        queueScheduler.manageTokenStatus();

        // then
        verify(queueFacade, times(1)).manageTokens();
    }
}
