package sube.interviews.mareoenvios.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sube.interviews.mareoenvios.dto.response.CustomerResponse;
import sube.interviews.mareoenvios.service.CustomerService;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Operaciones sobre compradores")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/info/{customerId}")
    @Operation(summary = "Obtener información de un comprador por ID")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    @GetMapping("/info")
    @Operation(summary = "Obtener listado de todos los compradores")
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(customerService.getAllCustomers(pageable));
    }
}
