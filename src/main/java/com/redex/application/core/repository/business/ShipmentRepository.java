package com.redex.application.core.repository.business;

import com.redex.application.core.model.business.Flight;
import com.redex.application.core.model.business.Shipment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.hibernate.loader.Loader.SELECT;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByCode(String code);

//    @Query(value = "SELECT * FROM localdb.shipment\n" +
//            "ORDER BY registration_date ASC\n" +
//            "LIMIT 1", nativeQuery = true)
//    Optional<Shipment> findFirst();
//    @Query(value = "SELECT * FROM localdb.shipment\n" +
//            "ORDER BY registration_date DESC\n" +
//            "LIMIT 1", nativeQuery = true)
//    Optional<Shipment> findLast();

    Optional<Shipment> findTopByOrderByRegistrationDatetimeAsc();
    Optional<Shipment> findTopByOrderByRegistrationDatetimeDesc();

    List<Shipment> findByRegistrationDatetimeAfterAndRegistrationDatetimeBeforeOrderByRegistrationDatetime(
            OffsetDateTime startDate, OffsetDateTime endDate
    );

    List<Shipment> findByRegistrationDatetimeGreaterThanEqualAndRegistrationDatetimeLessThan(
            OffsetDateTime startDate, OffsetDateTime endDate, Sort sort
    );

    List<Shipment> findBySimulationEquals(Long simulation);
}
