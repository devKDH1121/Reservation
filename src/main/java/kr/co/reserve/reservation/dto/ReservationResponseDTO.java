package kr.co.reserve.reservation.dto;

import kr.co.reserve.reservation.entity.Reservation;
import kr.co.reserve.reservation.entity.ReservationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ReservationResponseDTO {

    private Long reservationId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status;
    private int total_price;

    // 예약 관련 사용자 / 장소 정보
    private UserInfoForReservationDTO userInfo;
    private ResourceInfoForReservationDTO resourceInfo;

    // 생성 / 수정
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 엔티티 -> DTO
    public ReservationResponseDTO(Reservation reservation) {
        this.reservationId = reservation.getReservationId();
        this.startTime = reservation.getStartTime();
        this.endTime = reservation.getEndTime();
        this.status = reservation.getStatus();
        this.total_price = reservation.getTotal_price();
        this.createdAt = reservation.getCreatedAt();
        this.updatedAt = reservation.getUpdatedAt();

        if(reservation.getUser() != null) {
            this.userInfo = new UserInfoForReservationDTO(
                    reservation.getUser().getUserId(),
                    reservation.getUser().getName(),
                    reservation.getUser().getPhone(),
                    reservation.getUser().getEmail()
            );
        }
        if(reservation.getResource() != null) {
            this.resourceInfo = new ResourceInfoForReservationDTO(
                    reservation.getResource().getResourceId(),
                    reservation.getResource().getName(),
                    reservation.getResource().getLocation(),
                    reservation.getResource().getPrice()
            );
        }
    }

}
