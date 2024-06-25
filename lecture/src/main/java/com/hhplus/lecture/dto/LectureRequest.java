package com.hhplus.lecture.dto;

import com.fasterxml.jackson.core.JsonToken;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class LectureRequest {
    private Long id;
}
