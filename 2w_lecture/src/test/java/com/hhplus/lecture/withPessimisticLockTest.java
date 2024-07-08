package com.hhplus.lecture;

import com.hhplus.lecture.controller.dto.LectureRequest;
import com.hhplus.lecture.domain.repository.LectureHistoryRepository;
import com.hhplus.lecture.domain.repository.LectureScheduleRepository;
import com.hhplus.lecture.domain.service.LectureService;
import com.hhplus.lecture.infra.entity.LectureSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class withPessimisticLockTest {
    @Autowired
    private LectureService lectureService;

    @Autowired
    private LectureScheduleRepository lectureScheduleRepository;

    @Autowired
    private LectureHistoryRepository lectureHistoryRepository;

    @BeforeEach
    public void setUp() {
        // 테스트를 위한 초기화 작업
        // 테스트 데이터를 초기화하는 코드 작성
        LectureSchedule lectureSchedule = LectureSchedule.builder()
                .id(30L)
                .lectureId(1L)
                .lectureDate(Timestamp.valueOf("2024-06-29 13:00:00"))
                .maxPersonnel(30)
                .currentPersonnel(0)
                .build();
        lectureScheduleRepository.save(lectureSchedule);
    }

    @Test
    @Transactional
    public void testApplyLectureWithPessimisticLock() throws InterruptedException {
        int numberOfUsers = 35;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfUsers);
        CountDownLatch latch = new CountDownLatch(numberOfUsers);
        Long lectureScheduleId = 30L; // 테스트할 특강 ID

        for (int i = 0; i < numberOfUsers; i++) {
            long userId = i + 1;
            executorService.execute(() -> {
                try {
                    LectureRequest request = new LectureRequest(userId, lectureScheduleId);
                    lectureService.applyLecture(request);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        LectureSchedule lectureSchedule = lectureScheduleRepository.findByIdWithLock(lectureScheduleId).orElseThrow();
        int finalPersonnel = lectureSchedule.getCurrentPersonnel();

        assertThat(finalPersonnel).isEqualTo(30);
        assertThatThrownBy(() -> lectureService.applyLecture(new LectureRequest(36L, lectureScheduleId)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("특강 신청이 마감되었습니다.");
    }
}

