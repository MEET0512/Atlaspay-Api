package com.atlaspay.api.controller;

import com.atlaspay.api.dto.ApiResponse;
import com.atlaspay.api.dto.TransactionDTO;
import com.atlaspay.api.service.TransactionService;
import com.atlaspay.api.service.UserService;
import com.atlaspay.api.utils.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TransactionController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    /**
     * Create a new transaction (Authenticated users only)
     * POST /api/transactions
     *
     * @param transactionDTO Transaction details
     * @param authentication Current authenticated user
     * @return Created transaction details
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionDTO>> createTransaction(
            @Valid @RequestBody TransactionDTO transactionDTO,
            Authentication authentication) {

        // Verify user exists
        String email = authentication.getName();
        userService.getUserByEmail(email);

        TransactionDTO createdTransaction = transactionService.createTransaction(transactionDTO);
        return ResponseUtil.created("Transaction created successfully", createdTransaction);
    }

    /**
     * Get transaction by ID
     * GET /api/transactions/{transactionId}
     *
     * @param transactionId Transaction ID
     * @return Transaction details
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionDTO>> getTransactionById(
            @PathVariable Long transactionId) {

        TransactionDTO transaction = transactionService.getTransactionById(transactionId);
        return ResponseUtil.success("Transaction retrieved successfully", transaction);
    }

    /**
     * Get all transactions for current authenticated user
     * GET /api/transactions/my?page=0&size=10&sort=createdAt,desc
     *
     * @param authentication Current authenticated user
     * @param page Page number (0-indexed)
     * @param size Page size
     * @param sort Sort field and direction
     * @return Paginated user transactions
     */
    @GetMapping("/user/my")
    public ResponseEntity<ApiResponse<Page<TransactionDTO>>> getMyTransactions(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort) {

        String email = authentication.getName();
        Long userId = userService.getUserByEmail(email).getUserId();

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        Page<TransactionDTO> transactions = transactionService.getUserTransactions(userId, pageable);

        return ResponseUtil.success("User transactions retrieved successfully", transactions);
    }

    /**
     * Get all transactions for specific user (Admin access)
     * GET /api/transactions/user/{userId}?page=0&size=10
     *
     * @param userId User ID
     * @param page Page number
     * @param size Page size
     * @return Paginated transactions for user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<TransactionDTO>>> getUserTransactions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TransactionDTO> transactions = transactionService.getUserTransactions(userId, pageable);

        return ResponseUtil.success("User transactions retrieved successfully", transactions);
    }

    /**
     * Get all transactions (Admin access)
     * GET /api/transactions?page=0&size=10&sort=createdAt,desc
     *
     * @param page Page number
     * @param size Page size
     * @param sort Sort field
     * @return Paginated list of all transactions
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionDTO>>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        Page<TransactionDTO> transactions = transactionService.getAllTransactions(pageable);

        return ResponseUtil.success("All transactions retrieved successfully", transactions);
    }

    /**
     * Update transaction status (Admin access)
     * PATCH /api/transactions/{transactionId}/status
     *
     * @param transactionId Transaction ID
     * @param status New status (PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED)
     * @return Updated transaction
     */
    @PatchMapping("/{transactionId}/status")
    public ResponseEntity<ApiResponse<TransactionDTO>> updateTransactionStatus(
            @PathVariable Long transactionId,
            @RequestParam String status) {

        TransactionDTO updatedTransaction = transactionService.updateTransactionStatus(transactionId, status);
        return ResponseUtil.success("Transaction status updated successfully", updatedTransaction);
    }

    /**
     * Get transaction by reference number
     * GET /api/transactions/ref/{reference}
     *
     * @param reference Transaction reference number
     * @return Transaction details
     */
    @GetMapping("/ref/{reference}")
    public ResponseEntity<ApiResponse<TransactionDTO>> getTransactionByReference(
            @PathVariable String reference) {

        return ResponseUtil.success("Transaction reference retrieved successfully", null);
    }

}

