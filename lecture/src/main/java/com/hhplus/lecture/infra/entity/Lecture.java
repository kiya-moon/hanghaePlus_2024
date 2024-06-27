package com.hhplus.lecture.infra.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Entity
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;    // pk

    @Column(name = "lecture_nm", nullable = false)
    private String lectureNm;   // 특강명

    @Column(name = "teacher_nm", nullable = false)
    private String teacherNm;   // 강사명

    @Column(name = "max_personnel")
    private Integer maxPersonnel = 30;   // 최대인원

}
