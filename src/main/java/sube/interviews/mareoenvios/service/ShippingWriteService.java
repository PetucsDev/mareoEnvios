package sube.interviews.mareoenvios.service;

import sube.interviews.mareoenvios.domain.ShippingState;
import sube.interviews.mareoenvios.dto.request.ShippingCreateRequest;
import sube.interviews.mareoenvios.dto.response.ShippingResponse;

/**
 * Contrato para las operaciones de escritura de Shipping.
 *
 * Separado de {@link ShippingService} (lectura).
 */
public interface ShippingWriteService {

    ShippingResponse createShipping(ShippingCreateRequest request);

    ShippingResponse transitionState(Long shippingId, ShippingState targetState);
}
