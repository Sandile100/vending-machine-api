package co.za.vendingmachineapi.repository;

import co.za.vendingmachineapi.entity.Cash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashRepository extends JpaRepository<Cash, Long> {
    Cash findDistinctByDenomination(Integer denomination);
}
