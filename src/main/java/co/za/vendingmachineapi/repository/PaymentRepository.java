package co.za.vendingmachineapi.repository;

import co.za.vendingmachineapi.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Sale, Long> {
}
