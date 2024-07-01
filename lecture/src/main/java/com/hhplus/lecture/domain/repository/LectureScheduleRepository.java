package com.hhplus.lecture.domain.repository;

import com.hhplus.lecture.infra.entity.LectureSchedule;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LectureScheduleRepository extends JpaRepository<LectureSchedule, Long>, LectureScheduleRepositoryCustom {
    // 비관적 락을 적용한 쿼리 메소드 추가
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ls FROM LectureSchedule ls WHERE ls.id = :id")
    Optional<LectureSchedule> findByIdWithLock(Long id);
}
