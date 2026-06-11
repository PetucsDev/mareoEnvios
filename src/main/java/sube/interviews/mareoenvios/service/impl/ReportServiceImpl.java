package sube.interviews.mareoenvios.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sube.interviews.mareoenvios.dto.response.TopProductResponse;
import sube.interviews.mareoenvios.repository.ReportRepository;
import sube.interviews.mareoenvios.service.ReportService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Override
    @Cacheable(value = "top-products")
    public List<TopProductResponse> getTop3Products() {
        return reportRepository.findTop3Products(PageRequest.of(0, 3));
    }
}
