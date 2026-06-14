package sube.interviews.mareoenvios.service;

import org.springframework.data.domain.Pageable;
import sube.interviews.mareoenvios.dto.response.CustomerResponse;
import sube.interviews.mareoenvios.dto.response.PagedResponse;

/**
 * Contrato público de la capa de service para Customer.
 * El controller depende de esta interface, nunca de la implementación concreta.
 */
public interface CustomerService {

    CustomerResponse getCustomerById(Long id);

    PagedResponse<CustomerResponse> getAllCustomers(Pageable pageable);
}
