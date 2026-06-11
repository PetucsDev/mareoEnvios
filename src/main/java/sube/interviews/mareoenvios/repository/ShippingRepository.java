package sube.interviews.mareoenvios.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sube.interviews.mareoenvios.domain.Shipping;
import sube.interviews.mareoenvios.domain.ShippingState;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long> {

    @Query("""
            SELECT s FROM Shipping s
            LEFT JOIN FETCH s.items i
            LEFT JOIN FETCH i.product
            WHERE s.id = :id
            """)
    Optional<Shipping> findByIdWithItems(@Param("id") Long id);

    @Query(
        value = """
            SELECT s FROM Shipping s
            LEFT JOIN FETCH s.items i
            LEFT JOIN FETCH i.product
            WHERE s.state = :state
            """,
        countQuery = "SELECT COUNT(s) FROM Shipping s WHERE s.state = :state"
    )
    Page<Shipping> findByState(@Param("state") ShippingState state, Pageable pageable);

    @Query(
        value = """
            SELECT s FROM Shipping s
            LEFT JOIN FETCH s.items i
            LEFT JOIN FETCH i.product
            WHERE s.sendDate BETWEEN :from AND :to
            """,
        countQuery = "SELECT COUNT(s) FROM Shipping s WHERE s.sendDate BETWEEN :from AND :to"
    )
    Page<Shipping> findBySendDateBetween(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            Pageable pageable
    );

}
