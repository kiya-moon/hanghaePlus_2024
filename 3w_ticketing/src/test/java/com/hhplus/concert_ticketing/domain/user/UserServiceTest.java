package com.hhplus.concert_ticketing.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 포인트_충전_성공_테스트() {
        // given
        Long userId = 1L;
        int initialBalance = 100;
        int chargeAmount = 50;
        int newBalance = initialBalance + chargeAmount;
        User user = new User(userId, initialBalance, 1);  // ID, Balance, Version

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.chargePoint(userId, newBalance)).thenReturn(1);  // Mock successful chargePoint operation
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, newBalance, 2)));  // Simulate new balance

        // when
        int result = userService.chargePoint(userId, chargeAmount);

        // then
        assertEquals(newBalance, result);  // Check the charge result
    }

    @Test
    void 포인트_충전_실패_테스트_낙관적_락_실패() {
        // given
        Long userId = 1L;
        int initialBalance = 100;
        int chargeAmount = 50;
        User user = new User(userId, initialBalance, 1);  // ID, Balance, Version

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.chargePoint(userId, initialBalance + chargeAmount)).thenReturn(0);  // Simulate optimistic lock failure

        // when / then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.chargePoint(userId, chargeAmount);
        });

        assertEquals("낙관적 락 예외. 다른 트랜잭션에서 엔티티가 수정되었습니다.", thrown.getMessage());
    }

    @Test
    void 포인트_충전_실패_테스트_잔액_불일치() {
        // given
        Long userId = 1L;
        int initialBalance = 100;
        int chargeAmount = 50;
        int incorrectBalance = 100;  // Incorrect balance to simulate failure
        User user = new User(userId, initialBalance, 1);  // ID, Balance, Version

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.chargePoint(userId, initialBalance + chargeAmount)).thenReturn(1);  // Mock successful chargePoint operation
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, incorrectBalance, 2)));  // Simulate balance mismatch

        // when / then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.chargePoint(userId, chargeAmount);
        });

        assertEquals("충전 결과 불일치. 예상 잔액: 150, 실제 잔액: 100", thrown.getMessage());
    }

    @Test
    void 사용자_정보_조회_성공_테스트() {
        // given
        Long userId = 1L;
        int balance = 100;
        User user = new User(userId, balance, 1);  // ID, Balance, Version

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        User result = userService.getUserInfo(userId);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(balance, result.getBalance());
    }

    @Test
    void 사용자_정보_조회_실패_테스트() {
        // given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when / then
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            userService.getUserInfo(userId);
        });

        assertEquals("사용자 정보가 없습니다.", thrown.getMessage());
    }

    @Test
    void 포인트_사용_성공_테스트() {
        // given
        Long userId = 1L;
        int initialBalance = 100;
        int useAmount = 50;
        int newBalance = initialBalance - useAmount;
        User user = new User(userId, initialBalance, 1);  // ID, Balance, Version

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.usePoint(userId, newBalance, 1)).thenReturn(1);  // Mock successful usePoint operation
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, newBalance, 2)));  // Simulate new balance

        // when
        int result = userService.usePoint(userId, useAmount);

        // then
        assertEquals(newBalance, result);  // Check the used point result
    }

    @Test
    void 포인트_사용_실패_테스트_낙관적_락_실패() {
        // given
        Long userId = 1L;
        int initialBalance = 100;
        int useAmount = 50;
        User user = new User(userId, initialBalance, 1);  // ID, Balance, Version

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.usePoint(userId, initialBalance - useAmount, 1)).thenReturn(0);  // Simulate optimistic lock failure

        // when / then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.usePoint(userId, useAmount);
        });

        assertEquals("낙관적 락 예외. 다른 트랜잭션에서 엔티티가 수정되었습니다.", thrown.getMessage());
    }
}
