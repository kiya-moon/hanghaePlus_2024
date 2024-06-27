package com.hhplus.lecture.domain.repository;

import com.hhplus.lecture.infra.entity.LectureSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureScheduleRepository extends JpaRepository<LectureSchedule, Long> {

}
