package kr.co.reserve.reservation.dto;

import kr.co.reserve.reservation.entity.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceResponseDTO {

    private Long resourceId;
    private String name;
    private String description;
    private String location;
    private int capacity;
    private int price;

    public ResourceResponseDTO(Resource resource) {
        this.resourceId = resource.getResourceId();
        this.name = resource.getName();
        this.description = resource.getDescription();
        this.location = resource.getLocation();
        this.capacity = resource.getCapacity();
        this.price = resource.getPrice();

    }
}
