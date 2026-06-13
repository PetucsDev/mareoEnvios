package sube.interviews.mareoenvios.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Wrapper estable para Page<T> que resuelve problemas de serialización en Redis.
 * 
 * Spring Data recomienda usar PagedModel pero no está disponible en esta versión.
 * Este wrapper proporciona una estructura JSON estable y serializable.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse<T> {
    
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean empty;
    
    /**
     * Convierte un Page<T> de Spring Data a PagedResponse<T> estable.
     */
    public static <T> PagedResponse<T> from(org.springframework.data.domain.Page<T> page) {
        return PagedResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}