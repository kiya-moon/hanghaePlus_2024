package com.hhplus.concert_ticketing.infra.config;

import com.hhplus.concert_ticketing.domain.concert.*;
import com.hhplus.concert_ticketing.domain.queue.Token;
import com.hhplus.concert_ticketing.domain.queue.TokenStatus;
import com.hhplus.concert_ticketing.infra.concert.ConcertOptionRepositoryImpl;
import com.hhplus.concert_ticketing.infra.concert.ConcertRepositoryImpl;
import com.hhplus.concert_ticketing.infra.concert.SeatRepositoryImpl;
import com.hhplus.concert_ticketing.infra.queue.QueueRepositoryImpl;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Timestamp;
import java.time.*;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

@Configuration
public class DataInitializer {

    private final ConcertRepositoryImpl concertRepositoryImpl;
    private final ConcertOptionRepositoryImpl concertOptionRepositoryImpl;
    private final SeatRepositoryImpl seatRepositoryImpl;
    private final QueueRepositoryImpl queueRepositoryImpl;


    public DataInitializer(ConcertRepositoryImpl concertRepositoryImpl, ConcertOptionRepositoryImpl concertOptionRepositoryImpl, SeatRepositoryImpl seatRepositoryImpl, QueueRepositoryImpl queueRepositoryImpl) {
        this.concertRepositoryImpl = concertRepositoryImpl;
        this.concertOptionRepositoryImpl = concertOptionRepositoryImpl;
        this.seatRepositoryImpl = seatRepositoryImpl;
        this.queueRepositoryImpl = queueRepositoryImpl;
    }

    @Bean
    public CommandLineRunner concertData() {
        return (args) -> {
            Faker faker = new Faker(new Locale("ko", "KOREA"));
            Random random = new Random();
            for (int i = 1; i <= 100; i++) {
                // 콘서트 이름 생성
                String concertName = faker.kpop().girlGroups() + " 콘서트";
                Concert concert = Concert.builder()
                        .name(concertName)
                        .build();
                concertRepositoryImpl.save(concert);

                // 콘서트에 대해 10개의 임의 날짜 생성
                for (int j = 0; j < 10; j++) {
                    // 랜덤 날짜 생성
                    LocalDate today = LocalDate.now();
                    LocalDate randomDate = today.plusDays(random.nextInt(365)); // 오늘로부터 최대 1년 후의 날짜

                    // 랜덤 시간 생성 (2시부터 7시)
                    int hour = 2 + random.nextInt(6); // 2부터 7까지의 시간

                    // LocalDateTime 생성 (정각)
                    LocalDateTime localDateTime = LocalDateTime.of(randomDate, LocalTime.of(hour, 0));

                    // LocalDateTime을 Timestamp로 변환
                    Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
                    Timestamp concertDate = Timestamp.from(instant);

                    ConcertOption concertOption = ConcertOption.builder()
                            .concertId(concert.getId())
                            .concertDate(concertDate)
                            .build();

                    concertOptionRepositoryImpl.save(concertOption);

                    // 최소 100,000원 이상, 최대 200,000원 이하의 만원 단위 가격 생성
                    int minPrice = 100000;
                    int maxPrice = 200000;
                    int price = minPrice + (random.nextInt((maxPrice - minPrice) / 10000 + 1)) * 10000;

                    // 좌석 생성
                    for (int k = 1; k <= 200; k++) {
                        String seatNumber = String.format("Seat-%03d", k);

                        Seat seatEntity = Seat.builder()
                                .concertOptionId(concertOption.getId())
                                .seatNumber(seatNumber)
                                .status(SeatStatus.UNLOCKED)
                                .price(price)
                                .build();
                        seatRepositoryImpl.save(seatEntity);
                    }
                }
            }
        };
    }

    @Bean
    public CommandLineRunner validToken() {
        return (args) -> {
            String token = UUID.randomUUID().toString();
            Long userId = 1L; // 임의의 userId 할당, 실제로는 적절한 로직에 따라 결정해야 함
            Timestamp createdAt = Timestamp.from(Instant.now());
            Timestamp expiresAt = Timestamp.from(Instant.now().plusSeconds(300)); // 5분 후 만료

            Token tokenEntity = Token.builder()
                    .token(token)
                    .userId(userId)
                    .status(TokenStatus.ACTIVE)
                    .createdAt(createdAt)
                    .expiresAt(expiresAt)
                    .build();
            queueRepositoryImpl.save(tokenEntity);
        };
    }
}
