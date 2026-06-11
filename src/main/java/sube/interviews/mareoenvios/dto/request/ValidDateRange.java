package sube.interviews.mareoenvios.dto.request;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que sendDate sea anterior o igual a arriveDate en ShippingCreateRequest.
 * Se aplica a nivel de clase para poder comparar ambos campos.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface ValidDateRange {

    String message() default "Send date must be before or equal to arrive date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
