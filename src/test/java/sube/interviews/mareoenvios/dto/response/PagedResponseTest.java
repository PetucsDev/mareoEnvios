package sube.interviews.mareoenvios.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PagedResponse — tests unitarios")
class PagedResponseTest {

    @Test
    @DisplayName("from(Page) debe convertir correctamente página con contenido")
    void from_shouldConvertPage_withContent() {
        // Arrange
        List<String> content = List.of("item1", "item2", "item3");
        PageRequest pageRequest = PageRequest.of(0, 20);
        Page<String> page = new PageImpl<>(content, pageRequest, 3);

        // Act
        PagedResponse<String> response = PagedResponse.from(page);

        // Assert
        assertThat(response.getContent()).isEqualTo(content);
        assertThat(response.getPageNumber()).isEqualTo(0);
        assertThat(response.getPageSize()).isEqualTo(20);
        assertThat(response.getTotalElements()).isEqualTo(3);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isTrue();
        assertThat(response.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("from(Page) debe convertir correctamente página vacía")
    void from_shouldConvertPage_isEmpty() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 20);
        Page<String> page = new PageImpl<>(Collections.emptyList(), pageRequest, 0);

        // Act
        PagedResponse<String> response = PagedResponse.from(page);

        // Assert
        assertThat(response.getContent()).isEmpty();
        assertThat(response.getPageNumber()).isEqualTo(0);
        assertThat(response.getPageSize()).isEqualTo(20);
        assertThat(response.getTotalElements()).isEqualTo(0);
        assertThat(response.getTotalPages()).isEqualTo(0);
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isTrue();
        assertThat(response.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("from(Page) debe manejar múltiples páginas")
    void from_shouldHandleMultiplePages() {
        // Arrange - Page 2 of 3
        List<String> content = List.of("item21", "item22");
        PageRequest pageRequest = PageRequest.of(1, 20); // Page 2 (0-indexed)
        Page<String> page = new PageImpl<>(content, pageRequest, 45);

        // Act
        PagedResponse<String> response = PagedResponse.from(page);

        // Assert
        assertThat(response.getContent()).isEqualTo(content);
        assertThat(response.getPageNumber()).isEqualTo(1);
        assertThat(response.getPageSize()).isEqualTo(20);
        assertThat(response.getTotalElements()).isEqualTo(45);
        assertThat(response.getTotalPages()).isEqualTo(3); // 45/20 = 3 pages
        assertThat(response.isFirst()).isFalse();
        assertThat(response.isLast()).isFalse();
        assertThat(response.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("from(Page) debe identificar primera página correctamente")
    void from_shouldIdentifyFirstPage() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<String> page = new PageImpl<>(List.of("item1"), pageRequest, 25);

        // Act
        PagedResponse<String> response = PagedResponse.from(page);

        // Assert
        assertThat(response.isFirst()).isTrue();
    }

    @Test
    @DisplayName("from(Page) debe identificar última página correctamente")
    void from_shouldIdentifyLastPage() {
        // Arrange - Last page
        List<String> content = List.of("item31");
        PageRequest pageRequest = PageRequest.of(3, 10); // Page 4 (0-indexed) - última página
        Page<String> page = new PageImpl<>(content, pageRequest, 31);

        // Act
        PagedResponse<String> response = PagedResponse.from(page);

        // Assert
        assertThat(response.isLast()).isTrue();
    }

    @Test
    @DisplayName("from(Page) debe manejar página intermedia correctamente")
    void from_shouldHandleMiddlePage() {
        // Arrange - Middle page
        List<String> content = List.of("item11", "item12");
        PageRequest pageRequest = PageRequest.of(1, 10); // Page 2 (0-indexed)
        Page<String> page = new PageImpl<>(content, pageRequest, 31);

        // Act
        PagedResponse<String> response = PagedResponse.from(page);

        // Assert
        assertThat(response.isFirst()).isFalse();
        assertThat(response.isLast()).isFalse();
    }

    @Test
    @DisplayName("builder debe crear instancia correctamente")
    void builder_shouldCreateInstance() {
        // Act
        PagedResponse<String> response = PagedResponse.<String>builder()
                .content(List.of("item1", "item2"))
                .pageNumber(0)
                .pageSize(10)
                .totalElements(2)
                .totalPages(1)
                .first(true)
                .last(true)
                .empty(false)
                .build();

        // Assert
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getPageNumber()).isEqualTo(0);
        assertThat(response.getPageSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isTrue();
        assertThat(response.isEmpty()).isFalse();
    }
}