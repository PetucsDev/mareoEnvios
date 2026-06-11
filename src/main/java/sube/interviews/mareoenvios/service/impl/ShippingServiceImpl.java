package sube.interviews.mareoenvios.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sube.interviews.mareoenvios.domain.*;
import sube.interviews.mareoenvios.dto.request.ShippingCreateRequest;
import sube.interviews.mareoenvios.dto.request.ShippingItemRequest;
import sube.interviews.mareoenvios.dto.response.ShippingResponse;
import sube.interviews.mareoenvios.event.ShippingStateChangedEvent;
import sube.interviews.mareoenvios.exception.BusinessException;
import sube.interviews.mareoenvios.exception.ResourceNotFoundException;
import sube.interviews.mareoenvios.mapper.CustomerMapper;
import sube.interviews.mareoenvios.mapper.ShippingMapper;
import sube.interviews.mareoenvios.repository.CustomerRepository;
import sube.interviews.mareoenvios.repository.ProductRepository;
import sube.interviews.mareoenvios.repository.ShippingRepository;
import sube.interviews.mareoenvios.service.BaseService;
import sube.interviews.mareoenvios.service.ShippingService;
import sube.interviews.mareoenvios.service.ShippingWriteService;
import sube.interviews.mareoenvios.shipping.factory.ShippingStateStrategyFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación concreta de ShippingService.
 * Extiende BaseService (Template Method) e implementa la interfaz pública ShippingService.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShippingServiceImpl extends BaseService<Shipping, Long> implements ShippingService, ShippingWriteService {

    private final ShippingRepository shippingRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ShippingMapper shippingMapper;
    private final CustomerMapper customerMapper;
    private final ShippingStateStrategyFactory strategyFactory;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    protected CrudRepository<Shipping, Long> getRepository() {
        return shippingRepository;
    }

    @Override
    protected String getEntityName() {
        return "Shipping";
    }

    @Override
    @Cacheable(value = "shipping", key = "#id")
    public ShippingResponse getShippingById(Long id) {
        Shipping shipping = shippingRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping not found with id: " + id));
        return shippingMapper.toResponse(shipping);
    }

    @Override
    @Cacheable(value = "shippings-by-date", key = "#from.toString() + '-' + #to.toString() + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ShippingResponse> getShippingsByDateRange(LocalDate from, LocalDate to, Pageable pageable) {
        return shippingRepository.findBySendDateBetween(from, to, pageable)
                .map(shippingMapper::toResponse);
    }

    @Override
    @Cacheable(value = "shippings-by-state", key = "#state.name() + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ShippingResponse> getShippingsByState(ShippingState state, Pageable pageable) {
        return shippingRepository.findByState(state, pageable)
                .map(shippingMapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"shipping", "shippings-by-state", "shippings-by-date"}, allEntries = true)
    public ShippingResponse createShipping(ShippingCreateRequest request) {
        Customer customer = resolveCustomer(request);
        List<ShippingItem> items = buildItems(request.getItems());

        // Builder pattern para construir la entidad Shipping
        Shipping shipping = Shipping.builder()
                .customer(customer)
                .sendDate(request.getSendDate())
                .arriveDate(request.getArriveDate())
                .priority(request.getPriority())
                .state(ShippingState.INITIAL)
                .items(new ArrayList<>())
                .build();

        items.forEach(item -> {
            item.setShipping(shipping);
            shipping.getItems().add(item);
        });

        return shippingMapper.toResponse(save(shipping));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"shipping", "shippings-by-state", "shippings-by-date"}, allEntries = true)
    public ShippingResponse transitionState(Long shippingId, ShippingState targetState) {
        Shipping shipping = findById(shippingId);
        ShippingState previousState = shipping.getState();

        // Strategy pattern: delega la transición a la estrategia correspondiente
        strategyFactory.getStrategy(targetState).apply(shipping);

        Shipping saved = shippingRepository.save(shipping);

        // Observer pattern: publica el evento de cambio de estado
        eventPublisher.publishEvent(
                new ShippingStateChangedEvent(this, shippingId, previousState, targetState)
        );

        return shippingMapper.toResponse(saved);
    }

    // --- helpers privados ---

    private Customer resolveCustomer(ShippingCreateRequest request) {
        if (request.getCustomerId() != null) {
            return customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new BusinessException(
                            "Customer not found with id: " + request.getCustomerId()
                    ));
        }
        if (request.getNewCustomer() == null) {
            throw new BusinessException("Either customerId or newCustomer must be provided");
        }
        return customerRepository.save(customerMapper.toEntity(request.getNewCustomer()));
    }

    private List<ShippingItem> buildItems(List<ShippingItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(req -> {
                    Product product = productRepository.findById(req.getProductId())
                            .orElseThrow(() -> new BusinessException(
                                    "Product not found with id: " + req.getProductId()
                            ));
                    // Builder pattern para construir cada ShippingItem
                    return ShippingItem.builder()
                            .product(product)
                            .productCount(req.getQuantity())
                            .build();
                })
                .toList();
    }
}
