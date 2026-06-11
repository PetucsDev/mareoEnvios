package sube.interviews.mareoenvios.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sube.interviews.mareoenvios.domain.Customer;
import sube.interviews.mareoenvios.dto.response.CustomerResponse;
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
        return customerMapper.toResponse(findById(id));
    }

    @Override
    @Cacheable(value = "customers-all", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customerMapper::toResponse);
    }
}
