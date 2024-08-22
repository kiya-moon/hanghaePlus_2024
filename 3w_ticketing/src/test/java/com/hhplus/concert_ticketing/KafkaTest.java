package com.hhplus.concert_ticketing;

import com.hhplus.concert_ticketing.interfaces.event.PaidReservationEventListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092"}, ports = {9092})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class KafkaTest {
    private static final Logger logger = LoggerFactory.getLogger(PaidReservationEventListener.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final List<String> messages = new ArrayList<>();
    private final CountDownLatch latch = new CountDownLatch(10);

    @KafkaListener(topics = "test_topic")
    public void listen(ConsumerRecord<String, String> record) {
        messages.add(record.value());
        latch.countDown();
    }

    @Test
    public void 카프카_메시지_발행_및_소비_테스트() throws Exception {
        String topic = "test_topic";
        String payload_even = "test_payload_even";
        String payload_odd = "test_payload_odd";

        // 메시지 발행 테스트
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                kafkaTemplate.send(new ProducerRecord<String, String>(topic, payload_even));
            } else {
                kafkaTemplate.send(new ProducerRecord<String, String>(topic, payload_odd));
            }
        }

        // 모든 메시지를 수신할 때까지 기다립니다.
        latch.await(10, TimeUnit.SECONDS);

        // 메시지 소비 테스트
        assertThat(messages).hasSize(10);
        logger.info("Consumed messages: " + messages);
    }
}
