package com.hhplus.concert_ticketing.infra.external;

import com.hhplus.concert_ticketing.domain.reservation.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DataPlatformMockApiClient {

    private static final Logger logger = LoggerFactory.getLogger(DataPlatformMockApiClient.class);
    private final RestTemplate restTemplate;

    private final String externalApiUrl = "https://example.com/api/sendReservationInfo"; // 외부 API 엔드포인트

    public DataPlatformMockApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendReservationInfo(Reservation reservation) {
        try {
            // 외부 API에 요청 보냄
            restTemplate.postForEntity(externalApiUrl, reservation, Void.class);
            logger.info("예약 정보 전송 성공: 예약 ID {}", reservation.getId());
        } catch (Exception e) {
            logger.error("예약 정보 전송 실패: {}", e.getMessage());
            throw e; // 필요한 경우 예외 처리
        }
    }
}
