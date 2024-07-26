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
        Double initialBalance = 100.0;
        Double chargeAmount = 50.0;
        Double newBalance = initialBalance + chargeAmount;
        UserEntity userEntity = new UserEntity(userId, initialBalance, 1);  // ID, Balance, Version

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.chargePoint(userId, newBalance, 1)).thenReturn(1);  // Mock successful chargePoint operation
        when(userRepository.findById(userId)).thenReturn(Optional.of(new UserEntity(userId, newBalance, 2)));  // Simulate new balance

        // when
        Double result = userService.chargePoint(userId, chargeAmount);

        // then
        assertEquals(newBalance, result);  // Check the charge result
    }

    @Test
    void 포인트_충전_실패_테스트_낙관적_락_실패() {
        // given
        Long userId = 1L;
        Double initialBalance = 100.0;
        Double chargeAmount = 50.0;
        UserEntity userEntity = new UserEntity(userId, initialBalance, 1);  // ID, Balance, Version

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.chargePoint(userId, initialBalance + chargeAmount, 1)).thenReturn(0);  // Simulate optimistic lock failure

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
        Double initialBalance = 100.0;
        Double chargeAmount = 50.0;
        Double incorrectBalance = 100.0;  // Incorrect balance to simulate failure
        UserEntity userEntity = new UserEntity(userId, initialBalance, 1);  // ID, Balance, Version

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.chargePoint(userId, initialBalance + chargeAmount, 1)).thenReturn(1);  // Mock successful chargePoint operation
        when(userRepository.findById(userId)).thenReturn(Optional.of(new UserEntity(userId, incorrectBalance, 2)));  // Simulate balance mismatch

        // when / then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.chargePoint(userId, chargeAmount);
        });

        assertEquals("충전 결과 불일치. 예상 잔액: 150.0, 실제 잔액: 100.0", thrown.getMessage());
    }

    @Test
    void 사용자_정보_조회_성공_테스트() {
        // given
        Long userId = 1L;
        Double balance = 100.0;
        UserEntity userEntity = new UserEntity(userId, balance, 1);  // ID, Balance, Version

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        // when
        UserEntity result = userService.getUserInfo(userId);

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
        Double initialBalance = 100.0;
        Double useAmount = 50.0;
        Double newBalance = initialBalance - useAmount;
        UserEntity userEntity = new UserEntity(userId, initialBalance, 1);  // ID, Balance, Version

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.usePoint(userId, newBalance, 1)).thenReturn(1);  // Mock successful usePoint operation
        when(userRepository.findById(userId)).thenReturn(Optional.of(new UserEntity(userId, newBalance, 2)));  // Simulate new balance

        // when
        Double result = userService.usePoint(userId, useAmount);

        // then
        assertEquals(newBalance, result);  // Check the used point result
    }

    @Test
    void 포인트_사용_실패_테스트_낙관적_락_실패() {
        // given
        Long userId = 1L;
        Double initialBalance = 100.0;
        Double useAmount = 50.0;
        UserEntity userEntity = new UserEntity(userId, initialBalance, 1);  // ID, Balance, Version

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.usePoint(userId, initialBalance - useAmount, 1)).thenReturn(0);  // Simulate optimistic lock failure

        // when / then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.usePoint(userId, useAmount);
        });

        assertEquals("낙관적 락 예외. 다른 트랜잭션에서 엔티티가 수정되었습니다.", thrown.getMessage());
    }
}
