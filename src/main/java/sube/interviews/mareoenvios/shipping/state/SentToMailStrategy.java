package sube.interviews.mareoenvios.shipping.state;

import org.springframework.stereotype.Component;
import sube.interviews.mareoenvios.domain.Shipping;
import sube.interviews.mareoenvios.domain.ShippingState;

@Component
public class SentToMailStrategy implements ShippingStateStrategy {

    @Override
    public ShippingState targetState() {
        return ShippingState.SENT_TO_MAIL;
    }

    @Override
    public void apply(Shipping shipping) {
        shipping.transitionTo(ShippingState.SENT_TO_MAIL);
    }
}
