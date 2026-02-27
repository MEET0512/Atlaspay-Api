package com.atlaspay.api.controller;

import com.atlaspay.api.dto.ApiResponse;
import com.atlaspay.api.dto.UserResponseDTO;
import com.atlaspay.api.service.UserService;
import com.atlaspay.api.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get current logged-in user profile
     * GET /api/users/me
     *
     * @param authentication Current authenticated user
     * @return User profile details
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getMyProfile(
            Authentication authentication) {

        String email = authentication.getName();
        UserResponseDTO userDTO = userService.getUserByEmail(email);
        return ResponseUtil.success("User profile retrieved successfully", userDTO);
    }

    /**
     * Get user by ID (Admin or self access)
     * GET /api/users/{userId}
     *
     * @param userId User ID to retrieve
     * @return User profile details
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(
            @PathVariable Long userId) {

        UserResponseDTO userDTO = userService.getUserById(userId);
        return ResponseUtil.success("User retrieved successfully", userDTO);
    }

    /**
     * Get all users with pagination (Admin access)
     * GET /api/users?page=0&size=10&sort=createdAt,desc
     *
     * @param page Page number (0-indexed)
     * @param size Page size
     * @param sort Sort field and direction
     * @return Paginated user list
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        Page<UserResponseDTO> users = userService.getAllUsers(pageable);
        return ResponseUtil.success("Users retrieved successfully", users);
    }

    /**
     * Update user profile
     * PUT /api/users/{userId}
     *
     * @param userId User ID to update
     * @param userDTO Updated user details
     * @return Updated user profile
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Long userId,
            @RequestBody UserResponseDTO userDTO) {

        UserResponseDTO updatedUser = userService.updateUser(userId, userDTO);
        return ResponseUtil.success("User updated successfully", updatedUser);
    }

    /**
     * Delete user account
     * DELETE /api/users/{userId}
     *
     * @param userId User ID to delete
     * @return Success message
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable Long userId) {

        userService.deleteUser(userId);
        return ResponseUtil.success("User deleted successfully", "User account has been deleted");
    }

    /**
     * Get user by email
     * GET /api/users/email/{email}
     *
     * @param email User email
     * @return User profile details
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserByEmail(
            @PathVariable String email) {

        UserResponseDTO userDTO = userService.getUserByEmail(email);
        return ResponseUtil.success("User retrieved successfully", userDTO);
    }

}
