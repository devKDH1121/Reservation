package kr.co.reserve.reservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceRequestDTO {

    @NotBlank(message = "자원 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "설명은 필수입니다.")
    private String description;

    @NotBlank(message = "위치는 필수입니다.")
    private String location;

    @Min(value = 1, message = "수용 인원은 최소 1명 이상이여야 합니다.")
    private int capacity;

    @Min(value = 0, message = "가격은 최소 0원부터입니다.")
    private int price;
}
