package com.atlaspay.api.service;

import com.atlaspay.api.dto.TransactionDTO;
import com.atlaspay.api.exception.ResourceNotFoundException;
import com.atlaspay.api.model.Transaction;
import com.atlaspay.api.model.User;
import com.atlaspay.api.model.transactionStatus;
import com.atlaspay.api.model.transactionType;
import com.atlaspay.api.repository.TransactionRepository;
import com.atlaspay.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService{

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        User user = userRepository.findById(transactionDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + transactionDTO.getUserId()));

        Transaction transaction = Transaction.builder()
                .user(user)
                .transactionReference(UUID.randomUUID().toString())
                .transactionType(transactionType.valueOf(transactionDTO.getTransactionType()))
                .amount(transactionDTO.getAmount())
                .status(transactionStatus.PENDING)
                .description(transactionDTO.getDescription())
                .recipientAccountNumber(transactionDTO.getRecipientAccountNumber())
                .recipientName(transactionDTO.getRecipientName())
                .fee(transactionDTO.getFee())
                .createdAt(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToDTO(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDTO getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with id: " + transactionId));
        return mapToDTO(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDTO> getUserTransactions(Long userId, Pageable pageable) {
        return transactionRepository.findByUserUserId(userId, pageable)
                .map(this::mapToDTO);
    }

    @Override
    public TransactionDTO updateTransactionStatus(Long transactionId, String status) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with id: " + transactionId));

        transaction.setStatus(transactionStatus.valueOf(status));

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return mapToDTO(updatedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDTO> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable)
                .map(this::mapToDTO);
    }


    private TransactionDTO mapToDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .transactionId(transaction.getTransactionId())
                .userId(transaction.getUser().getUserId())
                .transactionReference(transaction.getTransactionReference())
                .transactionType(transaction.getTransactionType().toString())
                .amount(transaction.getAmount())
                .status(transaction.getStatus().toString())
                .description(transaction.getDescription())
                .recipientAccountNumber(transaction.getRecipientAccountNumber())
                .recipientName(transaction.getRecipientName())
                .fee(transaction.getFee())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
