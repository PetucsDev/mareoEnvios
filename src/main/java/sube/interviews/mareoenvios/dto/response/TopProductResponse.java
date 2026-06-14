package sube.interviews.mareoenvios.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * DTO para el reporte de productos más solicitados.
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
