package sube.interviews.mareoenvios.shipping.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sube.interviews.mareoenvios.domain.ShippingState;
import sube.interviews.mareoenvios.exception.ResourceNotFoundException;
import sube.interviews.mareoenvios.shipping.state.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ShippingStateStrategyFactory — tests unitarios")
class ShippingStateStrategyFactoryTest {

    private ShippingStateStrategyFactory factory;

    @BeforeEach
    void setUp() {
        factory = new ShippingStateStrategyFactory(List.of(
                new SentToMailStrategy(),
                new InTravelStrategy(),
                new DeliveredStrategy(),
                new CancelledStrategy()
        ));
    }

    @Test
    @DisplayName("getStrategy debe retornar SentToMailStrategy para SENT_TO_MAIL")
    void getStrategy_shouldReturnSentToMailStrategy() {
        ShippingStateStrategy strategy = factory.getStrategy(ShippingState.SENT_TO_MAIL);
        assertThat(strategy).isInstanceOf(SentToMailStrategy.class);
    }

    @Test
    @DisplayName("getStrategy debe retornar InTravelStrategy para IN_TRAVEL")
    void getStrategy_shouldReturnInTravelStrategy() {
        ShippingStateStrategy strategy = factory.getStrategy(ShippingState.IN_TRAVEL);
        assertThat(strategy).isInstanceOf(InTravelStrategy.class);
    }

    @Test
    @DisplayName("getStrategy debe retornar DeliveredStrategy para DELIVERED")
    void getStrategy_shouldReturnDeliveredStrategy() {
        ShippingStateStrategy strategy = factory.getStrategy(ShippingState.DELIVERED);
        assertThat(strategy).isInstanceOf(DeliveredStrategy.class);
    }

    @Test
    @DisplayName("getStrategy debe retornar CancelledStrategy para CANCELLED")
    void getStrategy_shouldReturnCancelledStrategy() {
        ShippingStateStrategy strategy = factory.getStrategy(ShippingState.CANCELLED);
        assertThat(strategy).isInstanceOf(CancelledStrategy.class);
    }

    @Test
    @DisplayName("getStrategy debe lanzar ResourceNotFoundException para estado sin estrategia")
    void getStrategy_shouldThrow_whenNoStrategyRegistered() {
        ShippingStateStrategyFactory emptyFactory = new ShippingStateStrategyFactory(List.of());

        assertThatThrownBy(() -> emptyFactory.getStrategy(ShippingState.SENT_TO_MAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("SENT_TO_MAIL");
    }

    @Test
    @DisplayName("Todas las estrategias deben tener el targetState correcto")
    void allStrategies_shouldHaveCorrectTargetState() {
        assertThat(factory.getStrategy(ShippingState.SENT_TO_MAIL).targetState()).isEqualTo(ShippingState.SENT_TO_MAIL);
        assertThat(factory.getStrategy(ShippingState.IN_TRAVEL).targetState()).isEqualTo(ShippingState.IN_TRAVEL);
        assertThat(factory.getStrategy(ShippingState.DELIVERED).targetState()).isEqualTo(ShippingState.DELIVERED);
        assertThat(factory.getStrategy(ShippingState.CANCELLED).targetState()).isEqualTo(ShippingState.CANCELLED);
    }
}
