package com.atlaspay.api.service;

import com.atlaspay.api.dto.TransactionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

    TransactionDTO createTransaction(TransactionDTO transactionDTO);

    TransactionDTO getTransactionById(Long transactionId);

    Page<TransactionDTO> getUserTransactions(Long userId, Pageable pageable);

    TransactionDTO updateTransactionStatus(Long transactionId, String status);

    Page<TransactionDTO> getAllTransactions(Pageable pageable);
}
