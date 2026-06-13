package sube.interviews.mareoenvios.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEvent;
import sube.interviews.mareoenvios.domain.ShippingState;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShippingStateChangedListener — tests unitarios")
class ShippingStateChangedListenerTest {

    private ShippingStateChangedListener listener;

    @BeforeEach
    void setUp() {
        listener = new ShippingStateChangedListener();
    }

    @Test
    @DisplayName("debe manejar evento de cambio de estado sin lanzar excepción")
    void onStateChanged_shouldHandleEvent_withoutThrowing() {
        ShippingStateChangedEvent event = new ShippingStateChangedEvent(
                this, 
                1L, 
                ShippingState.INITIAL, 
                ShippingState.SENT_TO_MAIL
        );

        // Should not throw any exception
        assertThatCode(() -> listener.onStateChanged(event))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("debe manejar todos los tipos de transiciones de estado")
    void onStateChanged_shouldHandleAllStateTransitions() {
        ShippingState[] states = ShippingState.values();
        
        for (int i = 0; i < states.length - 1; i++) {
            ShippingState from = states[i];
            ShippingState to = states[i + 1];
            
            ShippingStateChangedEvent event = new ShippingStateChangedEvent(
                    this, 
                    1L, 
                    from, 
                    to
            );

            assertThatCode(() -> listener.onStateChanged(event))
                    .doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("debe manejar evento con shipping ID nulo")
    void onStateChanged_shouldHandleNullShippingId() {
        ShippingStateChangedEvent event = new ShippingStateChangedEvent(
                this, 
                null, 
                ShippingState.INITIAL, 
                ShippingState.SENT_TO_MAIL
        );

        assertThatCode(() -> listener.onStateChanged(event))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("debe manejar evento con estados nulos")
    void onStateChanged_shouldHandleNullStates() {
        ShippingStateChangedEvent event = new ShippingStateChangedEvent(
                this, 
                1L, 
                null, 
                null
        );

        assertThatCode(() -> listener.onStateChanged(event))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("debe manejar evento consigo mismo como source")
    void onStateChanged_shouldHandleSelfAsSource() {
        ShippingStateChangedListener selfListener = new ShippingStateChangedListener();
        
        ShippingStateChangedEvent event = new ShippingStateChangedEvent(
                selfListener, 
                1L, 
                ShippingState.INITIAL, 
                ShippingState.SENT_TO_MAIL
        );

        assertThatCode(() -> selfListener.onStateChanged(event))
                .doesNotThrowAnyException();
    }
}