package com.hhplus.lecture.infra.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Entity
public class LectureSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;    // pk

    @Column(name = "lecture_id", nullable = false)
    private Long lectureId;   // 특강 ID

    @Column(name = "lecture_date", nullable = false)
    private Timestamp lectureDate;

    @Column(name = "max_personnel")
    private Integer maxPersonnel = 30;   // 최대인원

    @Column(name = "current_personnel")
    private Integer currentPersonnel;   // 현재인원
}
