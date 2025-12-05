package kr.co.reserve.reservation.controller;

import jakarta.validation.Valid;
import kr.co.reserve.reservation.dto.ResourceRequestDTO;
import kr.co.reserve.reservation.dto.ResourceResponseDTO;
import kr.co.reserve.reservation.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    // 등록
    @PostMapping("/create")
    public ResponseEntity<ResourceResponseDTO> create(@RequestBody ResourceRequestDTO dto) {
        ResourceResponseDTO responseDTO = resourceService.create(dto);

        return ResponseEntity.ok(responseDTO);
    }

    // 상세 조회
    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceResponseDTO> getResourceById(@PathVariable Long resourceId) {
        ResourceResponseDTO resourceResponseDTO = resourceService.getResourceById(resourceId);

        return ResponseEntity.ok(resourceResponseDTO);
    }
    // 수정
    @PatchMapping("/{resourceId}")
    public ResponseEntity<ResourceResponseDTO> updateResource(@PathVariable Long resourceId, @Valid @RequestBody ResourceRequestDTO requestDTO) {
        ResourceResponseDTO resourceResponseDTO = resourceService.updateResource(resourceId, requestDTO);

        return ResponseEntity.ok(resourceResponseDTO);
    }

    // 삭제
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long resourceId) {
        resourceService.deleteResource(resourceId);

        return ResponseEntity.noContent().build();
    }

    // 모두 조회
    @GetMapping("/list")
    public ResponseEntity<List<ResourceResponseDTO>> getAllResources() {

        return ResponseEntity.ok(resourceService.getAllResources());
    }
}
