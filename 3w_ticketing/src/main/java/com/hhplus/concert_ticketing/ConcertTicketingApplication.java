package com.hhplus.concert_ticketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class ConcertTicketingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConcertTicketingApplication.class, args);
	}

}
