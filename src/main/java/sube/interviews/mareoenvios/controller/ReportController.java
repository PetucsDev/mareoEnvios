package sube.interviews.mareoenvios.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sube.interviews.mareoenvios.dto.response.TopProductResponse;
import sube.interviews.mareoenvios.service.ReportService;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Reportes y estadísticas")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/topSended")
    @Operation(summary = "Top 3 productos más solicitados para envío")
    public ResponseEntity<List<TopProductResponse>> getTop3Products() {
        return ResponseEntity.ok(reportService.getTop3Products());
    }
}
