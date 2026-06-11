package sube.interviews.mareoenvios.shipping.state;

import sube.interviews.mareoenvios.domain.Shipping;
import sube.interviews.mareoenvios.domain.ShippingState;

/**
 * Strategy pattern: cada implementación encapsula la lógica
 * de transición hacia un estado destino concreto.
 */
public interface ShippingStateStrategy {

    /** Estado destino que aplica esta estrategia. */
    ShippingState targetState();

    /** Ejecuta la transición sobre el envío. */
    void apply(Shipping shipping);
}
