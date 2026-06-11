package sube.interviews.mareoenvios.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * DTO para el reporte de productos más solicitados.
 *
 * Se instancia desde una JPQL constructor expression en ShippingRepository.
 * @JsonCreator + @JsonProperty permiten que Jackson (y por ende Redis/GenericJackson2JsonRedisSerializer)
 * pueda deserializar correctamente la clase inmutable generada por @Value.
 */
@Value
public class TopProductResponse {

    String description;
    Long totalQuantity;

    @JsonCreator
    public TopProductResponse(
            @JsonProperty("description") String description,
            @JsonProperty("totalQuantity") Long totalQuantity) {
        this.description = description;
        this.totalQuantity = totalQuantity;
    }
}
