package sube.interviews.mareoenvios.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sube.interviews.mareoenvios.domain.ShippingState;
import sube.interviews.mareoenvios.dto.request.ShippingCreateRequest;
import sube.interviews.mareoenvios.dto.request.ShippingItemRequest;
import sube.interviews.mareoenvios.dto.response.ShippingResponse;
import sube.interviews.mareoenvios.service.ShippingWriteService;
import sube.interviews.mareoenvios.service.impl.ShippingRetryWrapper;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShippingRetryWrapper — tests unitarios")
class ShippingRetryWrapperTest {

    @Mock
    private ShippingWriteService shippingWriteService;

    @InjectMocks
    private ShippingRetryWrapper wrapper;

    private ShippingCreateRequest createRequest;
    private ShippingResponse shippingResponse;

    @BeforeEach
    void setUp() {
        createRequest = ShippingCreateRequest.builder()
                .customerId(1L)
                .sendDate(LocalDate.now())
                .priority(1)
                .items(List.of(ShippingItemRequest.builder().productId(1L).quantity(1).build()))
                .build();

        shippingResponse = ShippingResponse.builder()
                .id(1L)
                .state(ShippingState.INITIAL)
                .build();
    }

    @Test
    @DisplayName("createShipping debe delegar al ShippingService y retornar su resultado")
    void createShipping_shouldDelegateToService() {
        when(shippingWriteService.createShipping(createRequest)).thenReturn(shippingResponse);

        ShippingResponse result = wrapper.createShipping(createRequest);

        assertThat(result).isEqualTo(shippingResponse);
        verify(shippingWriteService).createShipping(createRequest);
    }

    @Test
    @DisplayName("transitionState debe delegar al ShippingService con el ID y estado correctos")
    void transitionState_shouldDelegateToService() {
        ShippingResponse transitioned = ShippingResponse.builder()
                .id(1L)
                .state(ShippingState.SENT_TO_MAIL)
                .build();

        when(shippingWriteService.transitionState(1L, ShippingState.SENT_TO_MAIL)).thenReturn(transitioned);

        ShippingResponse result = wrapper.transitionState(1L, ShippingState.SENT_TO_MAIL);

        assertThat(result.getState()).isEqualTo(ShippingState.SENT_TO_MAIL);
        verify(shippingWriteService).transitionState(1L, ShippingState.SENT_TO_MAIL);
    }

    @Test
    @DisplayName("createShipping debe propagar excepción del service")
    void createShipping_shouldPropagateException() {
        when(shippingWriteService.createShipping(any())).thenThrow(new RuntimeException("DB error"));

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> wrapper.createShipping(createRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB error");
    }

    @Test
    @DisplayName("transitionState debe propagar excepción del service")
    void transitionState_shouldPropagateException() {
        when(shippingWriteService.transitionState(anyLong(), any())).thenThrow(new RuntimeException("DB error"));

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> wrapper.transitionState(1L, ShippingState.SENT_TO_MAIL))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB error");
    }
}
