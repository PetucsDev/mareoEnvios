package sube.interviews.mareoenvios.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import sube.interviews.mareoenvios.dto.request.ShippingCreateRequest;
import sube.interviews.mareoenvios.dto.response.ShippingResponse;
import sube.interviews.mareoenvios.domain.ShippingState;
import sube.interviews.mareoenvios.exception.BusinessException;
import sube.interviews.mareoenvios.exception.InvalidStateTransitionException;
import sube.interviews.mareoenvios.exception.ResourceNotFoundException;
import sube.interviews.mareoenvios.service.ShippingWriteService;

/**
 * Wrapper de reintentos para operaciones de escritura de Shipping.
 *
 * Separa @Retryable de @Transactional en beans distintos para garantizar
 * que cada reintento abra una transacción nueva y limpia a través del proxy
 * transaccional de ShippingServiceImpl.
 *
 * Orden de proxies: RetryInterceptor → TransactionInterceptor → lógica de negocio
 *
 * Solo reintenta ante errores de infraestructura (DB, Redis).
 * Las excepciones de negocio (BusinessException, ResourceNotFoundException,
 * InvalidStateTransitionException) no se reintentan — son deterministas.
 */
@Service("shippingRetryWrapper")
public class ShippingRetryWrapper implements ShippingWriteService {

    private final ShippingWriteService shippingWriteService;

    public ShippingRetryWrapper(@Qualifier("shippingServiceImpl") ShippingWriteService shippingWriteService) {
        this.shippingWriteService = shippingWriteService;
    }

    @Override
    @Retryable(
            retryFor = {DataAccessException.class, RedisConnectionFailureException.class},
            noRetryFor = {BusinessException.class, ResourceNotFoundException.class, InvalidStateTransitionException.class},
            maxAttemptsExpression = "${spring.retry.max-attempts:3}",
            backoff = @Backoff(delay = 500, multiplier = 2, random = true)
    )
    public ShippingResponse createShipping(ShippingCreateRequest request) {
        return shippingWriteService.createShipping(request);
    }

    @Override
    @Retryable(
            retryFor = {DataAccessException.class, RedisConnectionFailureException.class},
            noRetryFor = {BusinessException.class, ResourceNotFoundException.class, InvalidStateTransitionException.class},
            maxAttemptsExpression = "${spring.retry.max-attempts:3}",
            backoff = @Backoff(delay = 500, multiplier = 2, random = true)
    )
    public ShippingResponse transitionState(Long shippingId, ShippingState targetState) {
        return shippingWriteService.transitionState(shippingId, targetState);
    }
}
