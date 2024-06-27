package com.hhplus.lecture.controller.dto;

import com.fasterxml.jackson.core.JsonToken;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LectureRequest {
    private Long userId;
    private Long lectureScheduleId;
}
