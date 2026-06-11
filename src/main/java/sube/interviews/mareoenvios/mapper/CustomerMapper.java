package sube.interviews.mareoenvios.mapper;

import org.springframework.stereotype.Component;
import sube.interviews.mareoenvios.domain.Customer;
import sube.interviews.mareoenvios.dto.request.CustomerRequest;
import sube.interviews.mareoenvios.dto.response.CustomerResponse;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .address(customer.getAddress())
                .city(customer.getCity())
                .build();
    }

    public Customer toEntity(CustomerRequest request) {
        return Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .address(request.getAddress())
                .city(request.getCity())
                .build();
    }
}
