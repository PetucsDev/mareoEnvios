package sube.interviews.mareoenvios.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import sube.interviews.mareoenvios.domain.*;
import sube.interviews.mareoenvios.dto.request.CustomerRequest;
import sube.interviews.mareoenvios.dto.request.ShippingCreateRequest;
import sube.interviews.mareoenvios.dto.request.ShippingItemRequest;
import sube.interviews.mareoenvios.dto.response.ShippingResponse;
import sube.interviews.mareoenvios.dto.response.PagedResponse;
import sube.interviews.mareoenvios.event.ShippingStateChangedEvent;
import sube.interviews.mareoenvios.exception.BusinessException;
import sube.interviews.mareoenvios.exception.InvalidStateTransitionException;
import sube.interviews.mareoenvios.exception.ResourceNotFoundException;
import sube.interviews.mareoenvios.mapper.CustomerMapper;
import sube.interviews.mareoenvios.mapper.ShippingMapper;
import sube.interviews.mareoenvios.repository.CustomerRepository;
import sube.interviews.mareoenvios.repository.ProductRepository;
import sube.interviews.mareoenvios.repository.ShippingRepository;
import sube.interviews.mareoenvios.service.impl.ShippingServiceImpl;
import sube.interviews.mareoenvios.shipping.factory.ShippingStateStrategyFactory;
import sube.interviews.mareoenvios.shipping.state.SentToMailStrategy;
import sube.interviews.mareoenvios.shipping.state.ShippingStateStrategy;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShippingServiceImpl — tests unitarios")
class ShippingServiceImplTest {

    @Mock private ShippingRepository shippingRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private ProductRepository productRepository;
    @Mock private ShippingMapper shippingMapper;
    @Mock private CustomerMapper customerMapper;
    @Mock private ShippingStateStrategyFactory strategyFactory;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ShippingServiceImpl shippingService;

    private Customer customer;
    private Product product;
    private Shipping shipping;
    private ShippingResponse shippingResponse;

    @BeforeEach
    void setUp() {
        customer = Customer.builder().id(1L).firstName("Juan").lastName("Pérez").build();
        product = Product.builder().id(1L).description("Notebook").weight(1.5).build();

        shipping = Shipping.builder()
                .id(1L)
                .customer(customer)
                .state(ShippingState.INITIAL)
                .sendDate(LocalDate.now())
                .priority(1)
                .build();

        shippingResponse = ShippingResponse.builder()
                .id(1L)
                .state(ShippingState.SENT_TO_MAIL)
                .build();
    }

    // --- getShippingById ---

    @Test
    @DisplayName("getShippingById debe retornar ShippingResponse cuando existe")
    void getShippingById_shouldReturnResponse_whenExists() {
        when(shippingRepository.findByIdWithItems(1L)).thenReturn(Optional.of(shipping));
        when(shippingMapper.toResponse(shipping)).thenReturn(shippingResponse);

        ShippingResponse result = shippingService.getShippingById(1L);

        assertThat(result).isEqualTo(shippingResponse);
        verify(shippingRepository).findByIdWithItems(1L);
    }

    @Test
    @DisplayName("getShippingById debe lanzar ResourceNotFoundException cuando no existe")
    void getShippingById_shouldThrow_whenNotFound() {
        when(shippingRepository.findByIdWithItems(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingService.getShippingById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- getShippingsByDateRange ---

    @Test
    @DisplayName("getShippingsByDateRange debe retornar página de envíos en el rango")
    void getShippingsByDateRange_shouldReturnPage() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);
        PageRequest pageable = PageRequest.of(0, 20);
        Page<Shipping> page = new PageImpl<>(List.of(shipping), pageable, 1);

        when(shippingRepository.findBySendDateBetween(from, to, pageable)).thenReturn(page);
        when(shippingMapper.toResponse(shipping)).thenReturn(shippingResponse);

        PagedResponse<ShippingResponse> result = shippingService.getShippingsByDateRange(from, to, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(20);
        verify(shippingRepository).findBySendDateBetween(from, to, pageable);
    }

    @Test
    @DisplayName("getShippingsByDateRange debe retornar página vacía si no hay envíos en el rango")
    void getShippingsByDateRange_shouldReturnEmpty_whenNoneFound() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2020, 1, 31);
        PageRequest pageable = PageRequest.of(0, 20);

        when(shippingRepository.findBySendDateBetween(from, to, pageable)).thenReturn(Page.empty());

        PagedResponse<ShippingResponse> result = shippingService.getShippingsByDateRange(from, to, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.isEmpty()).isTrue();
    }

    // --- getShippingsByState ---

    @Test
    @DisplayName("getShippingsByState debe retornar página de envíos por estado")
    void getShippingsByState_shouldReturnPage() {
        PageRequest pageable = PageRequest.of(0, 20);
        Page<Shipping> page = new PageImpl<>(List.of(shipping), pageable, 1);

        when(shippingRepository.findByState(ShippingState.INITIAL, pageable)).thenReturn(page);
        when(shippingMapper.toResponse(shipping)).thenReturn(shippingResponse);

        PagedResponse<ShippingResponse> result = shippingService.getShippingsByState(ShippingState.INITIAL, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(20);
    }

    // --- transitionState ---

    @Test
    @DisplayName("transitionState debe aplicar la estrategia y publicar el evento")
    void transitionState_shouldApplyStrategyAndPublishEvent() {
        ShippingStateStrategy strategy = new SentToMailStrategy();

        when(shippingRepository.findById(1L)).thenReturn(Optional.of(shipping));
        when(strategyFactory.getStrategy(ShippingState.SENT_TO_MAIL)).thenReturn(strategy);
        when(shippingRepository.save(any(Shipping.class))).thenReturn(shipping);
        when(shippingMapper.toResponse(any())).thenReturn(shippingResponse);

        ShippingResponse result = shippingService.transitionState(1L, ShippingState.SENT_TO_MAIL);

        assertThat(result).isNotNull();
        assertThat(shipping.getState()).isEqualTo(ShippingState.SENT_TO_MAIL);
        verify(eventPublisher).publishEvent(any(ShippingStateChangedEvent.class));
        verify(shippingRepository).save(shipping);
    }

    @Test
    @DisplayName("transitionState debe lanzar InvalidStateTransitionException para transición inválida")
    void transitionState_shouldThrow_whenInvalidTransition() {
        when(shippingRepository.findById(1L)).thenReturn(Optional.of(shipping));

        ShippingStateStrategy strategy = new SentToMailStrategy() {
            @Override
            public void apply(Shipping s) {
                s.transitionTo(ShippingState.IN_TRAVEL); // saltar SENT_TO_MAIL
            }
        };
        when(strategyFactory.getStrategy(ShippingState.IN_TRAVEL)).thenReturn(strategy);

        assertThatThrownBy(() -> shippingService.transitionState(1L, ShippingState.IN_TRAVEL))
                .isInstanceOf(InvalidStateTransitionException.class);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("transitionState debe lanzar ResourceNotFoundException cuando el envío no existe")
    void transitionState_shouldThrow_whenShippingNotFound() {
        when(shippingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingService.transitionState(99L, ShippingState.SENT_TO_MAIL))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // --- createShipping con customerId existente ---

    @Test
    @DisplayName("createShipping con customerId debe buscar el customer y crear el envío")
    void createShipping_withExistingCustomerId_shouldCreateShipping() {
        ShippingCreateRequest request = ShippingCreateRequest.builder()
                .customerId(1L)
                .sendDate(LocalDate.now())
                .arriveDate(LocalDate.now().plusDays(5))
                .priority(1)
                .items(List.of(ShippingItemRequest.builder().productId(1L).quantity(2).build()))
                .build();

        ShippingResponse expected = ShippingResponse.builder().id(1L).state(ShippingState.INITIAL).build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(shippingRepository.save(any(Shipping.class))).thenReturn(shipping);
        when(shippingMapper.toResponse(any())).thenReturn(expected);

        ShippingResponse result = shippingService.createShipping(request);

        assertThat(result.getState()).isEqualTo(ShippingState.INITIAL);
        verify(customerRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(shippingRepository).save(any(Shipping.class));
    }

    @Test
    @DisplayName("createShipping con newCustomer debe crear el customer y el envío")
    void createShipping_withNewCustomer_shouldCreateCustomerAndShipping() {
        CustomerRequest newCustomer = CustomerRequest.builder()
                .firstName("Ana")
                .lastName("García")
                .build();

        ShippingCreateRequest request = ShippingCreateRequest.builder()
                .newCustomer(newCustomer)
                .sendDate(LocalDate.now())
                .priority(1)
                .items(List.of(ShippingItemRequest.builder().productId(1L).quantity(1).build()))
                .build();

        ShippingResponse expected = ShippingResponse.builder().id(2L).state(ShippingState.INITIAL).build();

        when(customerMapper.toEntity(newCustomer)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(shippingRepository.save(any(Shipping.class))).thenReturn(shipping);
        when(shippingMapper.toResponse(any())).thenReturn(expected);

        ShippingResponse result = shippingService.createShipping(request);

        assertThat(result).isNotNull();
        verify(customerRepository).save(customer);
        verify(customerRepository, never()).findById(any());
    }

    @Test
    @DisplayName("createShipping sin customerId ni newCustomer debe lanzar BusinessException")
    void createShipping_withoutCustomerData_shouldThrow() {
        ShippingCreateRequest request = ShippingCreateRequest.builder()
                .sendDate(LocalDate.now())
                .priority(1)
                .items(List.of(ShippingItemRequest.builder().productId(1L).quantity(1).build()))
                .build();

        assertThatThrownBy(() -> shippingService.createShipping(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("customerId or newCustomer");
    }

    @Test
    @DisplayName("createShipping con customerId inexistente debe lanzar BusinessException")
    void createShipping_withNonExistentCustomerId_shouldThrow() {
        ShippingCreateRequest request = ShippingCreateRequest.builder()
                .customerId(99L)
                .sendDate(LocalDate.now())
                .priority(1)
                .items(List.of(ShippingItemRequest.builder().productId(1L).quantity(1).build()))
                .build();

        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingService.createShipping(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("createShipping con productId inexistente debe lanzar BusinessException")
    void createShipping_withNonExistentProductId_shouldThrow() {
        ShippingCreateRequest request = ShippingCreateRequest.builder()
                .customerId(1L)
                .sendDate(LocalDate.now())
                .priority(1)
                .items(List.of(ShippingItemRequest.builder().productId(99L).quantity(1).build()))
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shippingService.createShipping(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("99");
    }
}
