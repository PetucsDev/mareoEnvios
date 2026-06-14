package sube.interviews.mareoenvios.service;

import org.springframework.data.domain.Pageable;
import sube.interviews.mareoenvios.domain.ShippingState;
import sube.interviews.mareoenvios.dto.response.ShippingResponse;
import sube.interviews.mareoenvios.dto.response.PagedResponse;

import java.time.LocalDate;

/**
 * Contrato de solo lectura para la capa de service de Shipping.
 *
 * Las operaciones de escritura (createShipping, transitionState) viven en
 * {@link ShippingWriteService}.
 */
public interface ShippingService {

    ShippingResponse getShippingById(Long id);

    PagedResponse<ShippingResponse> getShippingsByDateRange(LocalDate from, LocalDate to, Pageable pageable);

    PagedResponse<ShippingResponse> getShippingsByState(ShippingState state, Pageable pageable);
}
