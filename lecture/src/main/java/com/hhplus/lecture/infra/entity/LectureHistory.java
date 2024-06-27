package com.hhplus.lecture.infra.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Entity
public class LectureHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;    // pk

    @Column(name = "lecture_schedule_id", nullable = false)
    private Long lectureScheduleId;   // 특강 일정 ID

    @Column(name = "user_id", nullable = false)
    private Long userId;   // 특강 신청 유저 ID

    @Column(name = "register_date", nullable = false)
    private Timestamp registerDate;
}
