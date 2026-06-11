package sube.interviews.mareoenvios.shipping.state;

import org.springframework.stereotype.Component;
import sube.interviews.mareoenvios.domain.Shipping;
import sube.interviews.mareoenvios.domain.ShippingState;

@Component
public class InTravelStrategy implements ShippingStateStrategy {

    @Override
    public ShippingState targetState() {
        return ShippingState.IN_TRAVEL;
    }

    @Override
    public void apply(Shipping shipping) {
        shipping.transitionTo(ShippingState.IN_TRAVEL);
    }
}
