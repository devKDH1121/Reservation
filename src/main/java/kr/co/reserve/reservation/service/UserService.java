package kr.co.reserve.reservation.service;

import kr.co.reserve.reservation.dto.JoinRequestDTO;
import kr.co.reserve.reservation.dto.UserResponseDTO;
import kr.co.reserve.reservation.dto.UserUpdateDTO;
import kr.co.reserve.reservation.entity.User;
import kr.co.reserve.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private static final String FOUND_NOT_USER = "사용자를 찾을 수 없습니다. : ";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Login
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(FOUND_NOT_USER + username));

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))

        );

    }

    // Join
    public UserResponseDTO join(JoinRequestDTO dto) {

        if(userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 사용자 입니다.");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole("ROLE_USER");


        User savedUser = userRepository.save(user);

        return new UserResponseDTO(savedUser);
    }

    // My Page ( 상세 보기 )
    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(FOUND_NOT_USER));

        return new UserResponseDTO(user);
    }


    // < For Admin >

    // 회원 삭제 (로그인 한 사용자도 가능)
    public UserResponseDTO deleteById(Long userId, String currentUsername) {
        User deleteUser = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(FOUND_NOT_USER));
        // 현재 로그인 사용자 조회
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> new UsernameNotFoundException(FOUND_NOT_USER + currentUsername));

        // 인가 확인
        boolean isUser = deleteUser.getUsername().equals(currentUser.getUsername());
        boolean isAdmin = currentUser.getRole().equals("ROLE_ADMIN");
        if(!isUser && !isAdmin) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        userRepository.delete(deleteUser);

        return new UserResponseDTO(deleteUser);
    }

    // 회원 수정 (로그인 한 사용자도 가능)
    public UserResponseDTO update(UserUpdateDTO dto, Long userId, String currentUsername) {
        User updateUser = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(FOUND_NOT_USER));
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> new UsernameNotFoundException(FOUND_NOT_USER + currentUsername));

        boolean isUser = updateUser.getUsername().equals(currentUser.getUsername());
        boolean isAdmin = currentUser.getRole().equals("ROLE_ADMIN");

        if(!isUser && !isAdmin) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        updateUser.setName(dto.getName());
        updateUser.setEmail(dto.getEmail());
        updateUser.setPhone(dto.getPhone());

        return new UserResponseDTO(updateUser);
    }

    // 회원 목록
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {

        return userRepository.findAll().stream().map(UserResponseDTO::new).collect(Collectors.toList());
    }

}
