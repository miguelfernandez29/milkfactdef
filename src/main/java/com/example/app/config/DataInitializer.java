package com.example.app.config;

import com.example.app.entity.Customer;
import com.example.app.entity.Product;
import com.example.app.entity.Transaction;
import com.example.app.repository.CustomerRepository;
import com.example.app.repository.ProductRepository;
import com.example.app.repository.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(CustomerRepository customerRepository,
                                       ProductRepository productRepository,
                                       TransactionRepository transactionRepository) {
        return args -> {
            Customer customer1 = new Customer();
            customer1.setName("John Doe");
            customer1.setAddressLine1("123 Main Street");
            customer1.setAddressLine2("Apt 4B");
            customer1.setPincode(110001L);
            customer1.setPhone(9876543210L);
            customerRepository.save(customer1);

            Customer customer2 = new Customer();
            customer2.setName("Jane Smith");
            customer2.setAddressLine1("456 Oak Avenue");
            customer2.setAddressLine2("Suite 100");
            customer2.setPincode(110002L);
            customer2.setPhone(9876543211L);
            customerRepository.save(customer2);

            Customer customer3 = new Customer();
            customer3.setName("Bob Wilson");
            customer3.setAddressLine1("789 Pine Road");
            customer3.setAddressLine2("Floor 2");
            customer3.setPincode(110003L);
            customer3.setPhone(9876543212L);
            customerRepository.save(customer3);

            Product product1 = new Product();
            product1.setName("Full Cream Milk");
            product1.setDescription("Fresh full cream milk - 1 Liter");
            product1.setPrice(new BigDecimal("60.00"));
            productRepository.save(product1);

            Product product2 = new Product();
            product2.setName("Toned Milk");
            product2.setDescription("Fresh toned milk - 1 Liter");
            product2.setPrice(new BigDecimal("50.00"));
            productRepository.save(product2);

            Product product3 = new Product();
            product3.setName("Skimmed Milk");
            product3.setDescription("Fresh skimmed milk - 1 Liter");
            product3.setPrice(new BigDecimal("45.00"));
            productRepository.save(product3);

            Product product4 = new Product();
            product4.setName("Curd");
            product4.setDescription("Fresh curd - 500g");
            product4.setPrice(new BigDecimal("40.00"));
            productRepository.save(product4);

            Product product5 = new Product();
            product5.setName("Paneer");
            product5.setDescription("Fresh paneer - 200g");
            product5.setPrice(new BigDecimal("80.00"));
            productRepository.save(product5);

            Transaction transaction1 = new Transaction();
            transaction1.setCustomer(customer1);
            transaction1.setProduct(product1);
            transaction1.setQuantity(2);
            transaction1.setPrice(product1.getPrice().multiply(BigDecimal.valueOf(2)));
            transaction1.setTransactionDate(LocalDate.now());
            transactionRepository.save(transaction1);

            Transaction transaction2 = new Transaction();
            transaction2.setCustomer(customer2);
            transaction2.setProduct(product2);
            transaction2.setQuantity(3);
            transaction2.setPrice(product2.getPrice().multiply(BigDecimal.valueOf(3)));
            transaction2.setTransactionDate(LocalDate.now());
            transactionRepository.save(transaction2);

            Transaction transaction3 = new Transaction();
            transaction3.setCustomer(customer1);
            transaction3.setProduct(product4);
            transaction3.setQuantity(1);
            transaction3.setPrice(product4.getPrice().multiply(BigDecimal.valueOf(1)));
            transaction3.setTransactionDate(LocalDate.now().minusDays(1));
            transactionRepository.save(transaction3);
        };
    }
}