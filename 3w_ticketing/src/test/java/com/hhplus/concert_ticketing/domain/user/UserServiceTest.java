package com.hhplus.concert_ticketing.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void 포인트_충전_성공_테스트() {
        Long userId = 1L;
        Double initialBalance = 100.0;
        Double chargeAmount = 50.0;
        Double newBalance = initialBalance + chargeAmount;
        UserEntity userEntity = new UserEntity(userId, initialBalance, 1);  // ID, Balance, Version

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        Mockito.when(userRepository.getBalance(userId)).thenReturn(Optional.of(initialBalance));
        Mockito.when(userRepository.chargePoint(userId, newBalance, 1)).thenReturn(1);  // Mock successful chargePoint operation
        Mockito.when(userRepository.getBalance(userId)).thenReturn(Optional.of(newBalance));  // Simulate new balance

        Double result = userService.chargePoint(userId, chargeAmount);

        assertEquals(newBalance, result);  // Check the charge result
    }

    @Test
    void 포인트_충전_실패_테스트_낙관적_락_실패() {
        Long userId = 1L;
        Double initialBalance = 100.0;
        Double chargeAmount = 50.0;
        UserEntity userEntity = new UserEntity(userId, initialBalance, 1);  // ID, Balance, Version

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        Mockito.when(userRepository.getBalance(userId)).thenReturn(Optional.of(initialBalance));
        Mockito.when(userRepository.chargePoint(userId, initialBalance + chargeAmount, 1)).thenReturn(0);  // Simulate optimistic lock failure

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.chargePoint(userId, chargeAmount);
        });

        assertEquals("낙관적 락 예외. 다른 트랜잭션에서 엔티티가 수정되었습니다.", thrown.getMessage());
    }

    @Test
    void 포인트_충전_실패_테스트_잔액_불일치() {
        Long userId = 1L;
        Double initialBalance = 100.0;
        Double chargeAmount = 50.0;
        Double incorrectBalance = 100.0;  // Incorrect balance to simulate failure

        UserEntity userEntity = new UserEntity(userId, initialBalance, 1);  // ID, Balance, Version

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        Mockito.when(userRepository.getBalance(userId)).thenReturn(Optional.of(initialBalance));
        Mockito.when(userRepository.chargePoint(userId, initialBalance + chargeAmount, 1)).thenReturn(1);  // Mock successful chargePoint operation
        Mockito.when(userRepository.getBalance(userId)).thenReturn(Optional.of(incorrectBalance));  // Simulate balance mismatch

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.chargePoint(userId, chargeAmount);
        });

        assertEquals("충전 결과 불일치. 예상 잔액: 150.0, 실제 잔액: 100.0", thrown.getMessage());
    }

    @Test
    void 사용자_잔액_조회_성공_테스트() {
        Long userId = 1L;
        Double balance = 100.0;

        Mockito.when(userRepository.getBalance(userId)).thenReturn(Optional.of(balance));

        Double result = userService.getBalance(userId);
        assertNotNull(result);
        assertEquals(balance, result);
    }

    @Test
    void 사용자_잔액_조회_실패_테스트() {
        Long userId = 1L;

        Mockito.when(userRepository.getBalance(userId)).thenReturn(Optional.empty());

        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
            userService.getBalance(userId);
        });

        assertEquals("접근이 유효하지 않습니다.", thrown.getMessage());
    }
}
