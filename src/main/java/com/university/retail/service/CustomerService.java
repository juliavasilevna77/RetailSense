package com.university.retail.service;

import com.university.retail.domain.CustomerDTO;
import com.university.retail.exception.CustomerNotFoundException;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    List<CustomerDTO> findAllCustomers();
    CustomerDTO createCustomer(CustomerDTO customerDTO);
    CustomerDTO findCustomerById(UUID id) throws CustomerNotFoundException;
    CustomerDTO updateCustomer(UUID id, CustomerDTO customerDTO) throws CustomerNotFoundException;
    void deleteCustomer(UUID id) throws CustomerNotFoundException;
}
