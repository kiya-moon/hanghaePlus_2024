package com.hhplus.concert_ticketing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ConcertTicketingApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class QueueIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testQueueProcessing() throws Exception {
        // 1. 토큰 발급 요청
        String requestJson = "{\"userId\": 300}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);

        ResponseEntity<String> tokenResponse = restTemplate.postForEntity("/queue/issue-token", requestEntity, String.class);

        // 상태 코드 검증
        assertEquals(HttpStatus.OK, tokenResponse.getStatusCode(), "토큰 발급 완료");

        // 응답 본문에서 토큰 추출 (단순 문자열 파싱)
        String token = extractTokenFromResponse(tokenResponse.getBody());

        // 2. 대기 시간 동안 기다림
        Thread.sleep(60000); // 1분 대기

        // 3. 대기열 상태 체크 요청
        ResponseEntity<String> checkResponse = restTemplate.getForEntity("/queue/check-queue?token=" + token, String.class);

        // 상태 코드 검증
        assertEquals(HttpStatus.OK, checkResponse.getStatusCode(), "Active 샹태 확인");
    }

    // 응답 본문에서 토큰을 추출하는 간단한 메서드
    private String extractTokenFromResponse(String responseBody) {
        // 간단한 파싱 로직 (JSON 파싱 라이브러리를 사용할 수도 있음)
        // 예를 들어, {"result":"200", "message":"토큰이 발급되었습니다.", "token":"your-token-value"}
        if (responseBody == null) {
            return "";
        }
        int startIndex = responseBody.indexOf("token\":\"") + 8;
        int endIndex = responseBody.indexOf("\"", startIndex);
        return responseBody.substring(startIndex, endIndex);
    }
}
