package com.example.app.repository;

import com.example.app.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCustomerId(Long customerId);

    List<Transaction> findByProductId(Long productId);

    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT t FROM Transaction t JOIN FETCH t.customer JOIN FETCH t.product")
    List<Transaction> findAllWithDetails();

    @Query("SELECT t FROM Transaction t JOIN FETCH t.customer JOIN FETCH t.product WHERE t.id = :id")
    Transaction findByIdWithDetails(@Param("id") Long id);
}