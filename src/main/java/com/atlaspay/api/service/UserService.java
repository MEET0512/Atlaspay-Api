package com.atlaspay.api.service;

import com.atlaspay.api.dto.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponseDTO getUserById(Long userId);

    UserResponseDTO getUserByEmail(String email);

    Page<UserResponseDTO> getAllUsers(Pageable pageable);

    UserResponseDTO updateUser(Long userId, UserResponseDTO userDTO);

    void deleteUser(Long userId);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
