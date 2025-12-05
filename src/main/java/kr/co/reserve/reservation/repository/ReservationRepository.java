package kr.co.reserve.reservation.repository;

import kr.co.reserve.reservation.entity.Reservation;
import kr.co.reserve.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsById(Long reservationId);

    // 이중 예약 방지 쿼리
    @Query("SELECT r from Reservation  r " +
           "WHERE r.resource.resourceId = :resourceId " +
           "AND r.status != :statusCanceled " +
           "AND (" +
           "    (:startTime < r.endTime) AND (:endTime > r.startTime)" +
           ")")
    List<Reservation> findConflictingReservation (
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("statusCanceled")ReservationStatus statusCanceled
            );

    // 나의 예약 목록 조회
    List<Reservation> findByUserId(Long userId);

}
