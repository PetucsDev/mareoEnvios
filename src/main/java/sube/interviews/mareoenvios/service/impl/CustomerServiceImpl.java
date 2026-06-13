package sube.interviews.mareoenvios.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sube.interviews.mareoenvios.domain.Customer;
import sube.interviews.mareoenvios.dto.response.CustomerResponse;
import sube.interviews.mareoenvios.dto.response.PagedResponse;
import sube.interviews.mareoenvios.mapper.CustomerMapper;
import sube.interviews.mareoenvios.repository.CustomerRepository;
import sube.interviews.mareoenvios.service.BaseService;
import sube.interviews.mareoenvios.service.CustomerService;

/**
 * Implementación concreta de CustomerService.
 * Extiende BaseService (Template Method) e implementa la interfaz pública CustomerService.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CustomerServiceImpl extends BaseService<Customer, Long> implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    protected CrudRepository<Customer, Long> getRepository() {
        return customerRepository;
    }

    @Override
    protected String getEntityName() {
        return "Customer";
    }

    @Override
    @Cacheable(value = "customers", key = "#id")
    public CustomerResponse getCustomerById(Long id) {
        log.debug("Fetching customer by ID: {}", id);
        CustomerResponse response = customerMapper.toResponse(findById(id));
        log.debug("Successfully fetched customer: {} {}", response.getFirstName(), response.getLastName());
        return response;
    }

    @Override
    @Cacheable(value = "customers-all", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public PagedResponse<CustomerResponse> getAllCustomers(Pageable pageable) {
        log.debug("Fetching all customers - Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<CustomerResponse> page = customerRepository.findAll(pageable)
                .map(customerMapper::toResponse);
        PagedResponse<CustomerResponse> response = PagedResponse.from(page);
        log.debug("Successfully fetched {} customers (total: {})", response.getContent().size(), response.getTotalElements());
        return response;
    }
}
