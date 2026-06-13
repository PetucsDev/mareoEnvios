package sube.interviews.mareoenvios.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DateRangeValidator — tests unitarios")
class DateRangeValidatorTest {

    private DateRangeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DateRangeValidator();
    }

    @Test
    @DisplayName("debe ser válido cuando arriveDate es null")
    void isValid_shouldReturnTrue_whenArriveDateIsNull() {
        ShippingCreateRequest request = ShippingCreateRequest.builder()
                .sendDate(LocalDate.now())
                .arriveDate(null)
                .build();

        boolean result = validator.isValid(request, null);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("debe ser válido cuando sendDate <= arriveDate")
    void isValid_shouldReturnTrue_whenSendDateIsBeforeOrEqualToArriveDate() {
        LocalDate sendDate = LocalDate.of(2024, 6, 15);
        LocalDate arriveDate = LocalDate.of(2024, 6, 20);

        ShippingCreateRequest request = ShippingCreateRequest.builder()
                .sendDate(sendDate)
                .arriveDate(arriveDate)
                .build();

        boolean result = validator.isValid(request, null);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("debe ser válido cuando sendDate == arriveDate")
    void isValid_shouldReturnTrue_whenSendDateEqualsArriveDate() {
        LocalDate sameDate = LocalDate.of(2024, 6, 15);

        ShippingCreateRequest request = ShippingCreateRequest.builder()
                .sendDate(sameDate)
                .arriveDate(sameDate)
                .build();

        boolean result = validator.isValid(request, null);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("debe ser inválido cuando sendDate > arriveDate")
    void isValid_shouldReturnFalse_whenSendDateIsAfterArriveDate() {
        LocalDate sendDate = LocalDate.of(2024, 6, 20);
        LocalDate arriveDate = LocalDate.of(2024, 6, 15); // Anterior a sendDate

        ShippingCreateRequest request = ShippingCreateRequest.builder()
                .sendDate(sendDate)
                .arriveDate(arriveDate)
                .build();

        boolean result = validator.isValid(request, null);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("debe ser válido cuando sendDate es null (aunque @NotNull lo maneje)")
    void isValid_shouldReturnTrue_whenSendDateIsNull() {
        ShippingCreateRequest request = ShippingCreateRequest.builder()
                .sendDate(null)
                .arriveDate(LocalDate.now())
                .build();

        boolean result = validator.isValid(request, null);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("debe manejar request null")
    void isValid_shouldHandleNullRequest() {
        boolean result = validator.isValid(null, null);

        assertThat(result).isTrue();
    }
}