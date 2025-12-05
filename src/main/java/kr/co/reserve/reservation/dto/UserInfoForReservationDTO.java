package kr.co.reserve.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoForReservationDTO {

    private Long userId;
    private String name;
    private String phone;
    private String email;
}
