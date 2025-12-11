package kr.co.reserve.reservation.controller;

import jakarta.validation.Valid;
import kr.co.reserve.reservation.dto.JoinRequestDTO;
import kr.co.reserve.reservation.dto.UserResponseDTO;
import kr.co.reserve.reservation.dto.UserUpdateDTO;
import kr.co.reserve.reservation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 가입
    @PostMapping("/join")
    public ResponseEntity<UserResponseDTO> join(@Valid @RequestBody JoinRequestDTO dto) {
        UserResponseDTO joinUser = userService.join(dto);

        return ResponseEntity.ok(joinUser);
    }
    // 상세 조회 (관리자 + 회원)
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long userId) {
        UserResponseDTO user = userService.findById(userId);

        return ResponseEntity.ok(user);
    }

    // 수정 (관리자 + 회원)
    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(@Valid @RequestBody UserUpdateDTO dto, @PathVariable Long userId, Principal principal) {
        String currentUsername = principal.getName();
        UserResponseDTO currentUser = userService.update(dto, userId, currentUsername);

        return ResponseEntity.ok(currentUser);

    }

    // 삭제 (관리자 + 회원)
    @DeleteMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> deleteUser(@PathVariable Long userId, Principal principal) {
        String currentUsername = principal.getName();

        userService.deleteById(userId, currentUsername);


        return  ResponseEntity.noContent().build();
    }

    // 전체 보기 (관리자만)
    @GetMapping("/list")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }
    // 내 정보 보기
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Principal principal) {
        UserResponseDTO user = userService.findUserByUsername(principal.getName());

        return ResponseEntity.ok(user);
    }
}
