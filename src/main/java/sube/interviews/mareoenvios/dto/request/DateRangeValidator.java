package sube.interviews.mareoenvios.dto.request;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementación del validador @ValidDateRange.
 * Pasa si arriveDate es null (opcional) o si sendDate <= arriveDate.
 */
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, ShippingCreateRequest> {

    @Override
    public boolean isValid(ShippingCreateRequest request, ConstraintValidatorContext context) {
        if (request.getSendDate() == null || request.getArriveDate() == null) {
            // sendDate es @NotNull y ya lo valida su propia anotación.
            // arriveDate es opcional: si no viene, no hay rango que validar.
            return true;
        }
        return !request.getSendDate().isAfter(request.getArriveDate());
    }
}
