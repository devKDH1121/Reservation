package kr.co.reserve.reservation.controller;

import jakarta.validation.Valid;
import kr.co.reserve.reservation.dto.ReservationRequestDTO;
import kr.co.reserve.reservation.dto.ReservationResponseDTO;
import kr.co.reserve.reservation.service.ReservationService;
import kr.co.reserve.reservation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final UserService userService;

    // Helper Method : Principal 이용해서 userId가져오기
    private Long getCurrentUserId(Principal principal) {
        return userService.findIdByUsername(principal.getName());
    }

    // 예약 리스트
    @GetMapping("/list")
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {

        return ResponseEntity.ok(reservationService.getAllReservations());
    }
    // 나의 예약 보기
    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponseDTO>> getMyReservations(Principal principal) {
        Long currentUserId = getCurrentUserId(principal);

        List<ReservationResponseDTO> reservations = reservationService.getMyReservations(currentUserId);

        return ResponseEntity.ok(reservations);
    }
    // 예약 상세보기
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResponseDTO> getReservation(@PathVariable("reservationId") Long reservationId) {
        ReservationResponseDTO reservation = reservationService.getReservationById(reservationId);

        return ResponseEntity.ok(reservation);
    }

    // 예약 등록
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(@Valid @RequestBody ReservationRequestDTO dto, Principal principal) {
        Long currentUserId = getCurrentUserId(principal);
        ReservationResponseDTO reservation = reservationService.createReservation(dto, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    // 예약 수정
    @PatchMapping("/{reservationId}")
    public ResponseEntity<ReservationResponseDTO> updateReservation(
            @Valid @RequestBody ReservationRequestDTO dto,
            @PathVariable Long reservationId,
            Principal principal
    ){
        Long currentUserId = getCurrentUserId(principal);

        ReservationResponseDTO updateReservation = reservationService.updateReservation(dto, reservationId, currentUserId);

        return ResponseEntity.ok(updateReservation);
    }


    // 예약 삭제
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long reservationId, Principal principal) {
        Long currentUseId = getCurrentUserId(principal);

        reservationService.deleteReservation(reservationId, currentUseId);

        return ResponseEntity.noContent().build();
    }


}
