package com.atlaspay.api.service;

import com.atlaspay.api.dto.UserResponseDTO;
import com.atlaspay.api.exception.ResourceNotFoundException;
import com.atlaspay.api.model.User;
import com.atlaspay.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepo;

    /**
     *
     * @param userId
     * @return
     */
    @Override
    public UserResponseDTO getUserById(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Not found with id:" + userId));
        return mapToDTO(user);
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable)
                .map(this::mapToDTO);
    }

    @Override
    public UserResponseDTO updateUser(Long userId, UserResponseDTO userDTO) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setFirst_name(userDTO.getFirstName());
        user.setLast_name(userDTO.getLastName());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        user.setCity(userDTO.getCity());
        user.setState(userDTO.getState());
        user.setZip_code(userDTO.getZipCode());
        user.setCountry(userDTO.getCountry());

        User updatedUser = userRepo.save(user);
        return mapToDTO(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public void deleteUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        userRepo.delete(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {
        return userRepo.existsByPhone(phone);
    }

    private UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUser_id())
                .firstName(user.getFirst_name())
                .lastName(user.getLast_name())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .zipCode(user.getZip_code())
                .country(user.getCountry())
                .status(String.valueOf(user.getStatus()))
                .createdAt(user.getCreated_at())
                .build();
    }
}
