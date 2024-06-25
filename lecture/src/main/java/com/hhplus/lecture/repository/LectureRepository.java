package com.hhplus.lecture.repository;

import com.hhplus.lecture.entity.LectureHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<LectureHistory, Long> {

}
