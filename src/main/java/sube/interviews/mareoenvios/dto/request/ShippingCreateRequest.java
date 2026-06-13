package sube.interviews.mareoenvios.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ValidDateRange
public class ShippingCreateRequest {

    // Si es null, se crea un nuevo customer con los datos de newCustomer
    Long customerId;

    @Valid
    CustomerRequest newCustomer;

    @NotNull(message = "Send date is required")
    LocalDate sendDate;

    LocalDate arriveDate;

    @NotNull(message = "Priority is required")
    Integer priority;

    @NotEmpty(message = "At least one item is required")
    @Valid
    List<ShippingItemRequest> items;
}
