package sube.interviews.mareoenvios.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sube.interviews.mareoenvios.domain.ShippingState;
import sube.interviews.mareoenvios.dto.response.ShippingResponse;

import java.time.LocalDate;

/**
 * Contrato de solo lectura para la capa de servicio de Shipping.
 *
 * Las operaciones de escritura (createShipping, transitionState) viven en
 * {@link ShippingWriteService}, siguiendo el principio de segregación de interfaces (ISP).
 */
public interface ShippingService {

    ShippingResponse getShippingById(Long id);

    Page<ShippingResponse> getShippingsByDateRange(LocalDate from, LocalDate to, Pageable pageable);

    Page<ShippingResponse> getShippingsByState(ShippingState state, Pageable pageable);
}
