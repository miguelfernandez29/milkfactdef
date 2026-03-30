package com.example.app.service;

import com.example.app.dto.CustomerDTO;
import com.example.app.dto.LookupDTO;
import com.example.app.entity.Customer;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDTO> findAll() {
        return customerRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO findById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return toDTO(customer);
    }

    public CustomerDTO create(CustomerDTO dto) {
        validateCustomerDTO(dto);
        Customer customer = toEntity(dto);
        customer.setId(null);
        Customer saved = customerRepository.save(customer);
        return toDTO(saved);
    }

    public CustomerDTO update(Long id, CustomerDTO dto) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        validateCustomerDTO(dto);
        Customer customer = toEntity(dto);
        customer.setId(id);
        Customer saved = customerRepository.save(customer);
        return toDTO(saved);
    }

    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return customerRepository.existsById(id);
    }

    public List<LookupDTO> getLookupList() {
        return customerRepository.findAll().stream()
                .map(c -> new LookupDTO(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }

    public Customer getEntityById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    private void validateCustomerDTO(CustomerDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (dto.getAddressLine1() == null || dto.getAddressLine1().trim().isEmpty()) {
            throw new IllegalArgumentException("Address Line 1 is required");
        }
        if (dto.getAddressLine2() == null || dto.getAddressLine2().trim().isEmpty()) {
            throw new IllegalArgumentException("Address Line 2 is required");
        }
        if (dto.getPincode() == null || dto.getPincode() <= 0) {
            throw new IllegalArgumentException("Valid pincode is required");
        }
        if (dto.getPhone() == null || dto.getPhone() <= 0) {
            throw new IllegalArgumentException("Valid phone number is required");
        }
    }

    private CustomerDTO toDTO(Customer entity) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAddressLine1(entity.getAddressLine1());
        dto.setAddressLine2(entity.getAddressLine2());
        dto.setPincode(entity.getPincode());
        dto.setPhone(entity.getPhone());
        return dto;
    }

    private Customer toEntity(CustomerDTO dto) {
        Customer entity = new Customer();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setAddressLine1(dto.getAddressLine1());
        entity.setAddressLine2(dto.getAddressLine2());
        entity.setPincode(dto.getPincode());
        entity.setPhone(dto.getPhone());
        return entity;
    }
}