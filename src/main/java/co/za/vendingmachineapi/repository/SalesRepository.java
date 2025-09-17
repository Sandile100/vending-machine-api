package co.za.vendingmachineapi.repository;

import co.za.vendingmachineapi.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SalesRepository extends JpaRepository <Sale, Long>{
    List<Sale> findAllByDateaddedBetween(Instant startDate, Instant endDate);
}
