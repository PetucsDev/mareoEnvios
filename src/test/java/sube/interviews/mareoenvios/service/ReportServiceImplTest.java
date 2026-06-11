package sube.interviews.mareoenvios.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import sube.interviews.mareoenvios.dto.response.TopProductResponse;
import sube.interviews.mareoenvios.repository.ReportRepository;
import sube.interviews.mareoenvios.service.impl.ReportServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportServiceImpl — tests unitarios")
class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    @DisplayName("getTop3Products debe retornar los 3 productos más solicitados")
    void getTop3Products_shouldReturnTop3() {
        List<TopProductResponse> expected = List.of(
                new TopProductResponse("Notebook", 50L),
                new TopProductResponse("Teclado", 30L),
                new TopProductResponse("Mouse", 20L)
        );
        when(reportRepository.findTop3Products(eq(PageRequest.of(0, 3)))).thenReturn(expected);

        List<TopProductResponse> result = reportService.getTop3Products();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getDescription()).isEqualTo("Notebook");
        assertThat(result.get(0).getTotalQuantity()).isEqualTo(50L);
        assertThat(result.get(1).getDescription()).isEqualTo("Teclado");
        assertThat(result.get(2).getDescription()).isEqualTo("Mouse");
        verify(reportRepository).findTop3Products(PageRequest.of(0, 3));
    }

    @Test
    @DisplayName("getTop3Products debe retornar lista vacía si no hay datos")
    void getTop3Products_shouldReturnEmptyList_whenNoData() {
        when(reportRepository.findTop3Products(any())).thenReturn(List.of());

        List<TopProductResponse> result = reportService.getTop3Products();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getTop3Products debe usar PageRequest con tamaño 3")
    void getTop3Products_shouldUsePageRequestOfSize3() {
        when(reportRepository.findTop3Products(any())).thenReturn(List.of());

        reportService.getTop3Products();

        verify(reportRepository).findTop3Products(PageRequest.of(0, 3));
    }
}
