package com.example.app.service;

import com.example.app.dto.TransactionDTO;
import com.example.app.entity.Customer;
import com.example.app.entity.Product;
import com.example.app.entity.Transaction;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerService customerService;
    private final ProductService productService;

    public TransactionService(TransactionRepository transactionRepository,
                              CustomerService customerService,
                              ProductService productService) {
        this.transactionRepository = transactionRepository;
        this.customerService = customerService;
        this.productService = productService;
    }

    public List<TransactionDTO> findAll() {
        return transactionRepository.findAllWithDetails().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TransactionDTO findById(Long id) {
        Transaction transaction = transactionRepository.findByIdWithDetails(id);
        if (transaction == null) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }
        return toDTO(transaction);
    }

    public TransactionDTO create(TransactionDTO dto) {
        validateTransactionDTO(dto);
        
        Customer customer = customerService.getEntityById(dto.getCustomerId());
        Product product = productService.getEntityById(dto.getProductId());
        
        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setProduct(product);
        transaction.setQuantity(dto.getQuantity());
        
        BigDecimal calculatedPrice = calculatePrice(product.getPrice(), dto.getQuantity());
        transaction.setPrice(calculatedPrice);
        
        LocalDate transactionDate = dto.getTransactionDate();
        if (transactionDate == null) {
            transactionDate = LocalDate.now();
        }
        transaction.setTransactionDate(transactionDate);
        
        Transaction saved = transactionRepository.save(transaction);
        return toDTO(saved);
    }

    public TransactionDTO update(Long id, TransactionDTO dto) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        
        validateTransactionDTO(dto);
        
        Customer customer = customerService.getEntityById(dto.getCustomerId());
        Product product = productService.getEntityById(dto.getProductId());
        
        existing.setCustomer(customer);
        existing.setProduct(product);
        existing.setQuantity(dto.getQuantity());
        
        BigDecimal calculatedPrice = calculatePrice(product.getPrice(), dto.getQuantity());
        existing.setPrice(calculatedPrice);
        
        LocalDate transactionDate = dto.getTransactionDate();
        if (transactionDate == null) {
            transactionDate = LocalDate.now();
        }
        existing.setTransactionDate(transactionDate);
        
        Transaction saved = transactionRepository.save(existing);
        return toDTO(saved);
    }

    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return transactionRepository.existsById(id);
    }

    public BigDecimal calculatePrice(BigDecimal productPrice, Integer quantity) {
        if (productPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return productPrice.multiply(BigDecimal.valueOf(quantity));
    }

    private void validateTransactionDTO(TransactionDTO dto) {
        if (dto.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (dto.getProductId() == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Valid quantity is required");
        }
        if (!customerService.existsById(dto.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId());
        }
        if (!productService.existsById(dto.getProductId())) {
            throw new ResourceNotFoundException("Product not found with id: " + dto.getProductId());
        }
    }

    private TransactionDTO toDTO(Transaction entity) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(entity.getId());
        dto.setCustomerId(entity.getCustomer().getId());
        dto.setCustomerName(entity.getCustomer().getName());
        dto.setProductId(entity.getProduct().getId());
        dto.setProductName(entity.getProduct().getName());
        dto.setQuantity(entity.getQuantity());
        dto.setPrice(entity.getPrice());
        dto.setTransactionDate(entity.getTransactionDate());
        return dto;
    }
}