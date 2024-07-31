package com.hhplus.concert_ticketing.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserEntityTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testVersionIncrement() {
        // Create a new user
        UserEntity user = new UserEntity();
        user.setBalance(10000);
        userRepository.save(user);

        // Fetch the user and check the version
        UserEntity fetchedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(0, fetchedUser.getVersion());

        // Update the user
        fetchedUser.setBalance(20000);
        userRepository.save(fetchedUser);

        // Fetch the user again and check the version
        UserEntity updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(1, updatedUser.getVersion());
    }
}

