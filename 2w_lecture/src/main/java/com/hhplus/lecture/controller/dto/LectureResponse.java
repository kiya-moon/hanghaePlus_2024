package com.hhplus.lecture.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LectureResponse {
    private Long id;

    private String lectureNm;

    private String teacherNm;

    private Integer maxPersonnel;
}
