package sube.interviews.mareoenvios.dto.response;

import lombok.Builder;
import lombok.Value;
import sube.interviews.mareoenvios.domain.ShippingState;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class ShippingResponse {
    Long id;
    CustomerResponse customer;
    ShippingState state;
    LocalDate sendDate;
    LocalDate arriveDate;
    Integer priority;
    List<ShippingItemResponse> items;
}
