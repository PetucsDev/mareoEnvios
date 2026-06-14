package sube.interviews.mareoenvios.shipping.factory;

import org.springframework.stereotype.Component;
import sube.interviews.mareoenvios.domain.ShippingState;
import sube.interviews.mareoenvios.exception.ResourceNotFoundException;
import sube.interviews.mareoenvios.shipping.state.ShippingStateStrategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory + Singleton pattern.
 *
 * Spring instancia este bean una sola vez (Singleton por defecto).
 * En el constructor recibe todas las implementaciones de ShippingStateStrategy
 * registradas como beans y las indexa por su estado destino en un Map inmutable.
 */
@Component
public class ShippingStateStrategyFactory {

    private final Map<ShippingState, ShippingStateStrategy> strategies;

    public ShippingStateStrategyFactory(List<ShippingStateStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toUnmodifiableMap(
                        ShippingStateStrategy::targetState,
                        Function.identity()
                ));
    }

    public ShippingStateStrategy getStrategy(ShippingState targetState) {
        ShippingStateStrategy strategy = strategies.get(targetState);
        if (strategy == null) {
            throw new ResourceNotFoundException(
                    "No strategy found for state: " + targetState.name()
            );
        }
        return strategy;
    }
}
