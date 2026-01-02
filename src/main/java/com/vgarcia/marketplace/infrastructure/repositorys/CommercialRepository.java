package com.vgarcia.marketplace.infrastructure.repositorys;

import com.vgarcia.marketplace.infrastructure.entities.Commercial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommercialRepository extends JpaRepository<Commercial, Long> {
    List<Commercial> findByIsPromotionalTrue();
    List<Commercial> findByTaxRateGreaterThan(BigDecimal mixtaxRate);
    List<Commercial> findByCurrency(String currency);
    List<Commercial> findByPromoStartDateAfter(LocalDateTime date);

    @Query("SELECT c FROM Commercial c WHERE c.promoEndDate < :currentDateTime AND c.isPromotional = true")
    List<Commercial> findExpiredPromotions(LocalDateTime currentDateTime);

    @Query(value = "SELECT c.id FROM commercials c WHERE c.promotional_price IS NULL AND c.discount_percentage > 0",
            nativeQuery = true)
    List<Long> findIdsWithDiscountButNoPromotionalPrice();


}
