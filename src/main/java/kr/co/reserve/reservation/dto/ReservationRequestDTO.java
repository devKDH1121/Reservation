package kr.co.reserve.reservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ReservationRequestDTO {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "장소 ID는 필수입니다.")
    private Long resourceId;

    @NotNull(message = "예약 시작 시간은 필수입니다.")
    @Future(message = "예약 시간은 현재 시간 이후여야 합니다.")
    private LocalDateTime startTime;

    @NotNull(message = "예약 종료 시간은 필수입니다.")
    private LocalDateTime endTime;


}
