package com.atlaspay.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    @NotNull(message = "Transaction id is require")
    private Long transactionId;

    @NotNull(message = "User ID is require.")
    private Long userId;

    private String transactionReference;

    @NotNull(message = "Transaction type is require.")
    private String transactionType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String status;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Pattern(regexp = "^[0-9]{10,20}$", message = "Recipient account number must be 10-20 digits")
    private String recipientAccountNumber;

    @Size(max = 100, message = "Recipient name cannot exceed 100 characters")
    private String recipientName;

    private BigDecimal fee;
    private String failureReason;
    private LocalDateTime createdAt;
}
