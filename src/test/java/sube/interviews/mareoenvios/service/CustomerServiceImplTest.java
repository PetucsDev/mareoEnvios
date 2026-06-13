package sube.interviews.mareoenvios.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sube.interviews.mareoenvios.domain.Customer;
import sube.interviews.mareoenvios.dto.response.CustomerResponse;
import sube.interviews.mareoenvios.dto.response.PagedResponse;
import sube.interviews.mareoenvios.exception.ResourceNotFoundException;
import sube.interviews.mareoenvios.mapper.CustomerMapper;
import sube.interviews.mareoenvios.repository.CustomerRepository;
import sube.interviews.mareoenvios.service.impl.CustomerServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerServiceImpl — tests unitarios")
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerResponse customerResponse;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .address("Av. Corrientes 1234")
                .city("Buenos Aires")
                .build();

        customerResponse = CustomerResponse.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .address("Av. Corrientes 1234")
                .city("Buenos Aires")
                .build();
    }

    @Test
    @DisplayName("getCustomerById debe retornar CustomerResponse cuando existe")
    void getCustomerById_shouldReturnResponse_whenExists() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        CustomerResponse result = customerService.getCustomerById(1L);

        assertThat(result).isEqualTo(customerResponse);
        verify(customerRepository).findById(1L);
    }

    @Test
    @DisplayName("getCustomerById debe lanzar ResourceNotFoundException cuando no existe")
    void getCustomerById_shouldThrow_whenNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    @DisplayName("getAllCustomers debe retornar página con customers")
    void getAllCustomers_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Customer> customerPage = new PageImpl<>(List.of(customer), pageable, 1);

        when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        PagedResponse<CustomerResponse> results = customerService.getAllCustomers(pageable);

        assertThat(results.getContent()).hasSize(1).contains(customerResponse);
        assertThat(results.getTotalElements()).isEqualTo(1);
        assertThat(results.getPageNumber()).isEqualTo(0);
        assertThat(results.getPageSize()).isEqualTo(20);
        assertThat(results.getTotalPages()).isEqualTo(1);
        assertThat(results.isFirst()).isTrue();
        assertThat(results.isLast()).isTrue();
        assertThat(results.isEmpty()).isFalse();
    }
}
