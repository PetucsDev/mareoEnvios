package sube.interviews.mareoenvios.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ShippingItemResponse {
    Long productId;
    String description;
    Double weight;
    Integer quantity;
}
