package com.university.retail.service.impl;

import com.university.retail.domain.CustomerDTO;
import com.university.retail.entity.CustomerEntity;
import com.university.retail.exception.CustomerNotFoundException;
import com.university.retail.repository.CustomerRepository;
import com.university.retail.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public List<CustomerDTO> findAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        CustomerEntity customer = CustomerEntity.builder()
                .username(customerDTO.getUsername())
                .email(customerDTO.getEmail())
                .age(customerDTO.getAge())
                .gender(customerDTO.getGender())
                .build();

        CustomerEntity savedCustomer = customerRepository.save(customer);
        return mapToDTO(savedCustomer);
    }

    @Override
    public CustomerDTO findCustomerById(UUID id) {
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
        return mapToDTO(customer);
    }

    @Override
    public CustomerDTO updateCustomer(UUID id, CustomerDTO customerDTO) {
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));

        customer = customer.toBuilder()
                .username(customerDTO.getUsername())
                .email(customerDTO.getEmail())
                .age(customerDTO.getAge())
                .gender(customerDTO.getGender())
                .build();

        CustomerEntity updatedCustomer = customerRepository.save(customer);
        return mapToDTO(updatedCustomer);
    }

    @Override
    public void deleteCustomer(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    private CustomerDTO mapToDTO(CustomerEntity customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .email(customer.getEmail())
                .age(customer.getAge())
                .gender(customer.getGender())
                .build();
    }
}
