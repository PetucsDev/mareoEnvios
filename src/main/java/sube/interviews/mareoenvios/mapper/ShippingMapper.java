package sube.interviews.mareoenvios.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sube.interviews.mareoenvios.domain.Shipping;
import sube.interviews.mareoenvios.domain.ShippingItem;
import sube.interviews.mareoenvios.dto.response.ShippingItemResponse;
import sube.interviews.mareoenvios.dto.response.ShippingResponse;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ShippingMapper {

    private final CustomerMapper customerMapper;

    public ShippingResponse toResponse(Shipping shipping) {
        return ShippingResponse.builder()
                .id(shipping.getId())
                .customer(customerMapper.toResponse(shipping.getCustomer()))
                .state(shipping.getState())
                .sendDate(shipping.getSendDate())
                .arriveDate(shipping.getArriveDate())
                .priority(shipping.getPriority())
                .items(toItemResponses(shipping.getItems()))
                .build();
    }

    public List<ShippingResponse> toResponseList(List<Shipping> shippings) {
        return shippings.stream()
                .map(this::toResponse)
                .toList();
    }

    private List<ShippingItemResponse> toItemResponses(List<ShippingItem> items) {
        return items.stream()
                .map(item -> ShippingItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .description(item.getProduct().getDescription())
                        .weight(item.getProduct().getWeight())
                        .quantity(item.getProductCount())
                        .build())
                .toList();
    }
}
