package sube.interviews.mareoenvios.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import sube.interviews.mareoenvios.domain.ShippingState;

/**
 * Observer pattern: evento publicado cada vez que un envío cambia de estado.
 * Desacopla la lógica de negocio de los side-effects (logging, auditoría, etc.).
 */
@Getter
public class ShippingStateChangedEvent extends ApplicationEvent {

    private final Long shippingId;
    private final ShippingState previousState;
    private final ShippingState newState;

    public ShippingStateChangedEvent(Object source,
                                     Long shippingId,
                                     ShippingState previousState,
                                     ShippingState newState) {
        super(source);
        this.shippingId = shippingId;
        this.previousState = previousState;
        this.newState = newState;
    }
}
