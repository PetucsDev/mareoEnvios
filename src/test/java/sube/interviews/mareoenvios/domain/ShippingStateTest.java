package sube.interviews.mareoenvios.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sube.interviews.mareoenvios.exception.InvalidStateTransitionException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ShippingState — reglas de transición")
class ShippingStateTest {

    @ParameterizedTest(name = "{0} → {1} debe ser válido")
    @MethodSource("validTransitions")
    void validTransitionShouldNotThrow(ShippingState from, ShippingState to) {
        assertThatNoException().isThrownBy(() -> from.validateTransition(to));
    }

    @ParameterizedTest(name = "{0} → {1} debe lanzar excepción")
    @MethodSource("invalidTransitions")
    void invalidTransitionShouldThrow(ShippingState from, ShippingState to) {
        assertThatThrownBy(() -> from.validateTransition(to))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    @Test
    @DisplayName("DELIVERED y CANCELLED son estados finales sin transiciones permitidas")
    void finalStatesShouldHaveNoAllowedTransitions() {
        assertThat(ShippingState.DELIVERED.allowedTransitions()).isEmpty();
        assertThat(ShippingState.CANCELLED.allowedTransitions()).isEmpty();
    }

    static Stream<Arguments> validTransitions() {
        return Stream.of(
                Arguments.of(ShippingState.INITIAL,      ShippingState.SENT_TO_MAIL),
                Arguments.of(ShippingState.INITIAL,      ShippingState.CANCELLED),
                Arguments.of(ShippingState.SENT_TO_MAIL, ShippingState.IN_TRAVEL),
                Arguments.of(ShippingState.SENT_TO_MAIL, ShippingState.CANCELLED),
                Arguments.of(ShippingState.IN_TRAVEL,    ShippingState.DELIVERED)
        );
    }

    static Stream<Arguments> invalidTransitions() {
        return Stream.of(
                Arguments.of(ShippingState.INITIAL,      ShippingState.IN_TRAVEL),
                Arguments.of(ShippingState.INITIAL,      ShippingState.DELIVERED),
                Arguments.of(ShippingState.SENT_TO_MAIL, ShippingState.INITIAL),
                Arguments.of(ShippingState.IN_TRAVEL,    ShippingState.CANCELLED),
                Arguments.of(ShippingState.DELIVERED,    ShippingState.INITIAL),
                Arguments.of(ShippingState.CANCELLED,    ShippingState.INITIAL)
        );
    }
}
