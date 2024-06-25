package com.hhplus.lecture.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;    // pk

    @Column(name = "lecture_nm", nullable = false)
    private String lectureNm;   // 특강명

    @Column(name = "application_date", nullable = false)
    private Timestamp applicationDate;   // 특강신청 오픈일

    @Column(name = "lecture_capacity", nullable = false)
    private String lectureCapacity;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lecture")
    private List<LectureHistory> LectureHistory;

}
