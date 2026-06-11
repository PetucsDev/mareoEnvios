package sube.interviews.mareoenvios.service;

import sube.interviews.mareoenvios.dto.response.TopProductResponse;

import java.util.List;

/**
 * Contrato público de la capa de servicio para reportes.
 */
public interface ReportService {

    List<TopProductResponse> getTop3Products();
}
