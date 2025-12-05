package kr.co.reserve.reservation.entity;

public enum ReservationStatus {
    // 예약 요청
    REQUESTED("요청"),

    // 예약 완료
    CONFIRMED("확정"),

    // 예약 취소
    CANCEL("취소");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
