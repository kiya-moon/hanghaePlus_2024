package com.hhplus.lecture.infra.impl;

import com.hhplus.lecture.domain.repository.LectureScheduleRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class LectureScheduleRepositoryImpl implements LectureScheduleRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Modifying
    @Transactional
    @Query("UPDATE LectureSchedule ls SET ls.currentPersonnel = :currentPersonnel WHERE ls.id = :id")
    public void updateCurrentPersonnel(Long id, int currentPersonnel) {
        entityManager.createQuery("UPDATE LectureSchedule ls SET ls.currentPersonnel = :currentPersonnel WHERE ls.id = :id")
                .setParameter("currentPersonnel", currentPersonnel)
                .setParameter("id", id)
                .executeUpdate();
    }

}
