package kr.co.reserve.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceInfoForReservationDTO {

    private Long resourceId;
    private String name;
    private String location;
    private int price;
}
