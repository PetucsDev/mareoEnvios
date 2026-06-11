package sube.interviews.mareoenvios.shipping.state;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sube.interviews.mareoenvios.domain.Customer;
import sube.interviews.mareoenvios.domain.Shipping;
import sube.interviews.mareoenvios.domain.ShippingState;
import sube.interviews.mareoenvios.exception.InvalidStateTransitionException;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ShippingStateStrategy implementations — tests unitarios")
class ShippingStateStrategyTest {

    private Shipping buildShipping(ShippingState state) {
        return Shipping.builder()
                .id(1L)
                .customer(Customer.builder().id(1L).firstName("Juan").lastName("Pérez").build())
                .state(state)
                .priority(1)
                .build();
    }

    // --- SentToMailStrategy ---

    @Test
    @DisplayName("SentToMailStrategy.apply debe transicionar INITIAL → SENT_TO_MAIL")
    void sentToMail_shouldTransitionFromInitial() {
        Shipping shipping = buildShipping(ShippingState.INITIAL);
        new SentToMailStrategy().apply(shipping);
        assertThat(shipping.getState()).isEqualTo(ShippingState.SENT_TO_MAIL);
    }

    @Test
    @DisplayName("SentToMailStrategy.apply debe lanzar excepción desde IN_TRAVEL")
    void sentToMail_shouldThrow_whenFromInTravel() {
        Shipping shipping = buildShipping(ShippingState.IN_TRAVEL);
        assertThatThrownBy(() -> new SentToMailStrategy().apply(shipping))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    @Test
    @DisplayName("SentToMailStrategy.targetState debe ser SENT_TO_MAIL")
    void sentToMail_targetState_shouldBeSentToMail() {
        assertThat(new SentToMailStrategy().targetState()).isEqualTo(ShippingState.SENT_TO_MAIL);
    }

    // --- InTravelStrategy ---

    @Test
    @DisplayName("InTravelStrategy.apply debe transicionar SENT_TO_MAIL → IN_TRAVEL")
    void inTravel_shouldTransitionFromSentToMail() {
        Shipping shipping = buildShipping(ShippingState.SENT_TO_MAIL);
        new InTravelStrategy().apply(shipping);
        assertThat(shipping.getState()).isEqualTo(ShippingState.IN_TRAVEL);
    }

    @Test
    @DisplayName("InTravelStrategy.apply debe lanzar excepción desde INITIAL")
    void inTravel_shouldThrow_whenFromInitial() {
        Shipping shipping = buildShipping(ShippingState.INITIAL);
        assertThatThrownBy(() -> new InTravelStrategy().apply(shipping))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    @Test
    @DisplayName("InTravelStrategy.targetState debe ser IN_TRAVEL")
    void inTravel_targetState_shouldBeInTravel() {
        assertThat(new InTravelStrategy().targetState()).isEqualTo(ShippingState.IN_TRAVEL);
    }

    // --- DeliveredStrategy ---

    @Test
    @DisplayName("DeliveredStrategy.apply debe transicionar IN_TRAVEL → DELIVERED")
    void delivered_shouldTransitionFromInTravel() {
        Shipping shipping = buildShipping(ShippingState.IN_TRAVEL);
        new DeliveredStrategy().apply(shipping);
        assertThat(shipping.getState()).isEqualTo(ShippingState.DELIVERED);
    }

    @Test
    @DisplayName("DeliveredStrategy.apply debe lanzar excepción desde INITIAL")
    void delivered_shouldThrow_whenFromInitial() {
        Shipping shipping = buildShipping(ShippingState.INITIAL);
        assertThatThrownBy(() -> new DeliveredStrategy().apply(shipping))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    @Test
    @DisplayName("DeliveredStrategy.targetState debe ser DELIVERED")
    void delivered_targetState_shouldBeDelivered() {
        assertThat(new DeliveredStrategy().targetState()).isEqualTo(ShippingState.DELIVERED);
    }

    // --- CancelledStrategy ---

    @Test
    @DisplayName("CancelledStrategy.apply debe transicionar INITIAL → CANCELLED")
    void cancelled_shouldTransitionFromInitial() {
        Shipping shipping = buildShipping(ShippingState.INITIAL);
        new CancelledStrategy().apply(shipping);
        assertThat(shipping.getState()).isEqualTo(ShippingState.CANCELLED);
    }

    @Test
    @DisplayName("CancelledStrategy.apply debe transicionar SENT_TO_MAIL → CANCELLED")
    void cancelled_shouldTransitionFromSentToMail() {
        Shipping shipping = buildShipping(ShippingState.SENT_TO_MAIL);
        new CancelledStrategy().apply(shipping);
        assertThat(shipping.getState()).isEqualTo(ShippingState.CANCELLED);
    }

    @Test
    @DisplayName("CancelledStrategy.apply debe lanzar excepción desde IN_TRAVEL")
    void cancelled_shouldThrow_whenFromInTravel() {
        Shipping shipping = buildShipping(ShippingState.IN_TRAVEL);
        assertThatThrownBy(() -> new CancelledStrategy().apply(shipping))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    @Test
    @DisplayName("CancelledStrategy.apply debe lanzar excepción desde DELIVERED")
    void cancelled_shouldThrow_whenFromDelivered() {
        Shipping shipping = buildShipping(ShippingState.DELIVERED);
        assertThatThrownBy(() -> new CancelledStrategy().apply(shipping))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    @Test
    @DisplayName("CancelledStrategy.targetState debe ser CANCELLED")
    void cancelled_targetState_shouldBeCancelled() {
        assertThat(new CancelledStrategy().targetState()).isEqualTo(ShippingState.CANCELLED);
    }
}
