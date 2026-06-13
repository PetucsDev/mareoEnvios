package sube.interviews.mareoenvios.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingItemResponse {
    Long productId;
    String description;
    Double weight;
    Integer quantity;
}
