package com.hhplus.concert_ticketing.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class UserServiceConcurrencyTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUsePointConcurrency() throws InterruptedException {
        Long userId = 1L;
        userRepository.save(new UserEntity(userId, 100.0));

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> userService.usePoint(userId, 50.0));
        executorService.submit(() -> userService.usePoint(userId, 50.0));

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("Final balance: " + user.getBalance());
    }
}