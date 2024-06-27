package com.hhplus.lecture.domain.repository;

import com.hhplus.lecture.infra.entity.LectureHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LectureHistoryRepository extends JpaRepository<LectureHistory, Long> {
    int countByLectureScheduleId(Long lectureScheduleId);
    Optional<LectureHistory> findByUserIdAndLectureScheduleId(Long userId, Long lectureScheduleId);
}
