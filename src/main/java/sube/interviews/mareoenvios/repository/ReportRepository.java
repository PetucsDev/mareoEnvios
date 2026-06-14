package sube.interviews.mareoenvios.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sube.interviews.mareoenvios.domain.ShippingItem;
import sube.interviews.mareoenvios.dto.response.TopProductResponse;

import java.util.List;

/**
 * Repositorio de reportes. Usa ShippingItem como raíz ya que las consultas
 * de agregación parten de esa entidad hacia Product.
 */
@Repository
public interface ReportRepository extends JpaRepository<ShippingItem, Long> {

    @Query("""
            SELECT new sube.interviews.mareoenvios.dto.response.TopProductResponse(
                p.description,
                SUM(si.productCount)
            )
            FROM ShippingItem si
            JOIN si.product p
            GROUP BY p.id, p.description
            ORDER BY SUM(si.productCount) DESC
            """)
    List<TopProductResponse> findTop3Products(Pageable pageable);
}
