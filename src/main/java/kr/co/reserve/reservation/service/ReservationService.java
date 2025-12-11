package kr.co.reserve.reservation.service;

import kr.co.reserve.reservation.dto.ReservationRequestDTO;
import kr.co.reserve.reservation.dto.ReservationResponseDTO;
import kr.co.reserve.reservation.entity.Reservation;
import kr.co.reserve.reservation.entity.ReservationStatus;
import kr.co.reserve.reservation.entity.Resource;
import kr.co.reserve.reservation.entity.User;
import kr.co.reserve.reservation.repository.ReservationRepository;
import kr.co.reserve.reservation.repository.ResourceRepository;
import kr.co.reserve.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;

    // 예약 리스트
    public List<ReservationResponseDTO> getAllReservations() {

        return reservationRepository.findAll().stream().map(ReservationResponseDTO::new).collect(Collectors.toList());
    }
    // 예약 상세보기
    public ReservationResponseDTO getReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new NoSuchElementException("예약을 찾을 수 없습니다 : " + reservationId));

        return new ReservationResponseDTO(reservation);
    }
    // 내 예약 조회
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getMyReservations(Long currentUserId) {

        return reservationRepository.findByUserId(currentUserId).stream()
                .map(ReservationResponseDTO::new)
                .collect(Collectors.toList());
    }

    // 예약 등록
    public ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO, Long currentUserId) {
        if(requestDTO.getEndTime().isBefore(requestDTO.getStartTime()) || requestDTO.getEndTime().equals(requestDTO.getStartTime())) {
            throw new IllegalArgumentException("종료 시간은 시작 시간 이후여야 합니다.");
        }
        // 장소 및 사용자 유효성 검증
        User user = userRepository.findById(currentUserId).orElseThrow(() -> new NoSuchElementException("예약자를 찾을 수 없습니다"));
        Resource resource = resourceRepository.findById(requestDTO.getResourceId()).orElseThrow(() -> new NoSuchElementException("예약 대상 장소를 찾을 수 없습니다."));

        // 이중 예약 방지 검사
        List<Reservation> duplicateReservation = reservationRepository.findConflictingReservation(
                requestDTO.getResourceId(),
                requestDTO.getStartTime(),
                requestDTO.getEndTime(),
                ReservationStatus.CANCEL
        );
        if(!duplicateReservation.isEmpty()) {
            throw new IllegalStateException("해당 장소는 이미 요청된 시간대에 예약이 존재합니다.");
        }
        long durationMinutes = Duration.between(requestDTO.getStartTime(), requestDTO.getEndTime()).toMinutes();
        double totalHours = durationMinutes / 60.0;
        int totalPrice = (int) Math.ceil(totalHours) * resource.getPrice();

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setResource(resource);
        reservation.setStartTime(requestDTO.getStartTime());
        reservation.setEndTime(requestDTO.getEndTime());
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setTotal_price(totalPrice);

        Reservation savedReservation = reservationRepository.save(reservation);

        return new ReservationResponseDTO(savedReservation);

    }
    // 예약 수정
    public ReservationResponseDTO updateReservation(ReservationRequestDTO requestDTO, Long reservationId, Long currentUserId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new NoSuchElementException("수정할 예약을 찾을 수 없습니다."));

        // 인가
        if(!reservation.getUser().getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("본인 예약만 수정할 수 있습니다");
        }
        // 예약시간 지났는지 확인
        if(reservation.getStartTime().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalStateException("이미 시작된 예약은 수정할 수 없습니다.");
        }
        // 시간 유효성
        if(reservation.getEndTime().isBefore(requestDTO.getStartTime()) || requestDTO.getEndTime().equals(requestDTO.getStartTime())) {
            throw new IllegalStateException("종료 시간은 시작 시간 이후여야 합니다.");
        }

        // 장소 및 금액 재계산 준비
        Resource resource = reservation.getResource();
        // 이중 예약 방지
        List<Reservation> duplicateReservation = reservationRepository.findConflictingReservation(
                requestDTO.getResourceId(),
                requestDTO.getStartTime(),
                requestDTO.getEndTime(),
                ReservationStatus.CANCEL
        );
        boolean hasConflict = duplicateReservation.stream().anyMatch(r -> !r.getReservationId().equals(reservationId));

        if(hasConflict) {
            throw new IllegalStateException("요청된 시간대에 이미 다른 예약이 존재합니다.");
        }

        // 금액 재계샨
        long durationMinutes = Duration.between(requestDTO.getStartTime(), reservation.getEndTime()).toMinutes();
        double totalHours = durationMinutes / 60.0;
        int newTotalPrice = (int) Math.ceil(totalHours) * resource.getPrice();

        // 예약 정보 업데이트
        reservation.setStartTime(requestDTO.getStartTime());
        reservation.setEndTime(requestDTO.getEndTime());
        reservation.setTotal_price(newTotalPrice);

        // 장소 ID가 변경되었을 경우
        if(!reservation.getResource().getResourceId().equals(resource.getResourceId())) {
            Resource newResource = resourceRepository.findById(requestDTO.getResourceId()).
                    orElseThrow(() ->  new NoSuchElementException("새 예약 대상 장소를 찾을 수 없습니다."));
            reservation.setResource(newResource);
        }

        return new  ReservationResponseDTO(reservation);
    }
    // 예약 삭제
    public ReservationResponseDTO deleteReservation(Long reservationId, Long currentUserID) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("삭제할 예약이 없습니다."));

        if(!reservation.getUser().getUserId().equals(currentUserID)) {
            throw new AccessDeniedException("삭제할 권한이 없습니다.");
        }

        // 예약 상태 확인
        if(reservation.getStatus().equals(ReservationStatus.CANCEL)) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }
        // 예약 시간이 이미 지났는지 확인
        if(reservation.getEndTime().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalStateException("이미 종료된 예약은 취소할 수 없습니다.");
        }

        reservation.setStatus(ReservationStatus.CANCEL);

        return new  ReservationResponseDTO(reservation);
    }




}
