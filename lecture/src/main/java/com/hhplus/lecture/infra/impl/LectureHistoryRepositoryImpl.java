package com.hhplus.lecture.infra.impl;

import com.hhplus.lecture.domain.repository.LectureHistoryRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class LectureHistoryRepositoryImpl implements LectureHistoryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Modifying
    @Query(value = "update lecture_history lh set lh.current_personnel = :newCurrentPersonnel where lh.lecture_schedule_id = :lectureScheduleId", nativeQuery = true)
    public void updateCurrentPersonnel(@Param("lectureScheduleId") Long lectureScheduleId, @Param("newCurrentPersonnel") int newCurrentPersonnel) {
        entityManager.createNativeQuery("update lecture_history lh set lh.current_personnel = :newCurrentPersonnel where lh.lecture_schedule_id = :lectureScheduleId")
                .setParameter("newCurrentPersonnel", newCurrentPersonnel)
                .setParameter("lectureScheduleId", lectureScheduleId)
                .executeUpdate();
    }

}
