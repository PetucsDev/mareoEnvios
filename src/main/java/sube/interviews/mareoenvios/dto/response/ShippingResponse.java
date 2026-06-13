package sube.interviews.mareoenvios.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import sube.interviews.mareoenvios.domain.ShippingState;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingResponse {
    Long id;
    CustomerResponse customer;
    ShippingState state;
    LocalDate sendDate;
    LocalDate arriveDate;
    Integer priority;
    List<ShippingItemResponse> items;
}
