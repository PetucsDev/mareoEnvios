package sube.interviews.mareoenvios.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Observer pattern: escucha los eventos de cambio de estado
 * sin acoplarse a la lógica de negocio que los genera.
 */
@Component
@Slf4j
public class ShippingStateChangedListener {

    @EventListener
    public void onStateChanged(ShippingStateChangedEvent event) {
        log.info(
                "[AUDIT] Shipping #{} transitioned from {} to {}",
                event.getShippingId(),
                event.getPreviousState(),
                event.getNewState()
        );
    }
}
