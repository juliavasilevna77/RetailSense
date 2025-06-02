package com.university.retail.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.university.retail.domain.CustomerDTO;
import com.university.retail.domain.enums.Gender;
import com.university.retail.entity.CustomerEntity;
import com.university.retail.exception.CustomerNotFoundException;
import com.university.retail.repository.CustomerRepository;
import com.university.retail.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerServiceImpl customerService;
    private UUID customerId;
    private CustomerEntity customerEntity;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(customerRepository);
        customerId = UUID.randomUUID();

        customerEntity = CustomerEntity.builder()
                .id(customerId)
                .username("customer1")
                .email("customer@example.com")
                .age(30)
                .gender(Gender.MALE)
                .build();

        customerDTO = CustomerDTO.builder()
                .username("customer1")
                .email("customer@example.com")
                .age(30)
                .gender(Gender.MALE)
                .build();
    }

    @Test
    void testFindAllCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(customerEntity));

        List<CustomerDTO> result = customerService.findAllCustomers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("customer1", result.get(0).getUsername());
        verify(customerRepository).findAll();
    }

    @Test
    void testCreateCustomer() {
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customerEntity);

        CustomerDTO result = customerService.createCustomer(customerDTO);

        assertNotNull(result);
        assertEquals("customer1", result.getUsername());
        verify(customerRepository).save(any(CustomerEntity.class));
    }

    @Test
    void testFindCustomerById() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));

        CustomerDTO result = customerService.findCustomerById(customerId);

        assertNotNull(result);
        assertEquals(customerId, result.getId());
        verify(customerRepository).findById(customerId);
    }

    @Test
    void testFindCustomerByIdNotFound() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.findCustomerById(customerId));
    }
}
