package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.dto.PointHistoryDTO;
import io.hhplus.tdd.point.dto.UserPointDTO;
import io.hhplus.tdd.point.vo.PointHistory;
import io.hhplus.tdd.point.vo.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPointDTO point(
            @PathVariable long id
    ) {
        UserPoint userPoint = pointService.getUserPoint(id);
        return new UserPointDTO(userPoint.id(), userPoint.point(), userPoint.updateMillis());
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistoryDTO> history(
            @PathVariable long id
    ) {
        List<PointHistory> histories = pointService.getPointHistory(id);
        List<PointHistoryDTO> historyDTOs = new ArrayList<>();
        for (PointHistory history : histories) {
            historyDTOs.add(new PointHistoryDTO(history.id(), history.userId(), history.amount(), history.type(), history.updateMillis()));
        }
        return historyDTOs;
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        pointService.chargePoint(id, amount);
        // 포인트 충전 후 업데이트 된 사용자 포인트 정보 반환
        return pointService.getUserPoint(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        boolean success = pointService.usePoint(id, amount);
        if (success) {
            return pointService.getUserPoint(id);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "포인트 사용에 실패했습니다. 잔고가 부족합니다.");
        }
    }
}
