package kr.co.reserve.reservation.service;

import kr.co.reserve.reservation.dto.ResourceRequestDTO;
import kr.co.reserve.reservation.dto.ResourceResponseDTO;
import kr.co.reserve.reservation.entity.Resource;
import kr.co.reserve.reservation.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;

    // 등록
    public ResourceResponseDTO create(ResourceRequestDTO dto) {
        if(resourceRepository.existsByName(dto.getName())) {
            throw new NoSuchElementException("이미 존재하는 장소입니다. : " + dto.getName());
        }

        Resource resource = new Resource();
        resource.setName(dto.getName());
        resource.setDescription(dto.getDescription());
        resource.setLocation(dto.getLocation());
        resource.setCapacity(dto.getCapacity());
        resource.setPrice(dto.getPrice());

        Resource savedResource = resourceRepository.save(resource);

        return new ResourceResponseDTO(savedResource);
    }


    // 상세 조회
    @Transactional(readOnly = true)
    public ResourceResponseDTO getResourceById(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(() -> new NoSuchElementException("자원을 찾을 수 없습니다."));

        return new ResourceResponseDTO(resource);
    }
    // 수정
    public ResourceResponseDTO updateResource(Long resourceId, ResourceRequestDTO requestDTO) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(() -> new NoSuchElementException("수정할 자원을 찾을 수 없습니다" + resourceId));

        if(!resource.getName().equals(requestDTO.getName())) {
            if(resourceRepository.existsByName(requestDTO.getName())) {
                throw new IllegalStateException("이미 존재하는 장소입니다.");
            }
        }

        resource.setName(requestDTO.getName());
        resource.setDescription(requestDTO.getDescription());
        resource.setLocation(requestDTO.getLocation());
        resource.setCapacity(requestDTO.getCapacity());
        resource.setPrice(requestDTO.getPrice());

        return new ResourceResponseDTO(resource);
    }

    // 삭제
    public void deleteResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(() -> new NoSuchElementException("삭제할 자원을 찾을 수 없습니다." + resourceId));

        resourceRepository.delete(resource);
    }

    // 모두 조회
    @Transactional(readOnly = true)
    public List<ResourceResponseDTO> getAllResources() {

        return resourceRepository.findAll().stream().map(ResourceResponseDTO::new).collect(Collectors.toList());
    }

}
