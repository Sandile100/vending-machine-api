package co.za.vendingmachineapi.service;

import co.za.vendingmachineapi.entity.Sale;
import co.za.vendingmachineapi.repository.SalesRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class SalesService {
    private final SalesRepository salesRepository;

    public SalesService(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }

    public void updateSales(final Sale sale) {
        salesRepository.save(sale);
    }

    public List<Sale> getAllSalesReport() {
        return salesRepository.findAll();
    }

    public List<Sale> getSalesReport(Instant startDate, Instant endDate) {
        return salesRepository.findAllByDateaddedBetween(startDate, endDate);
    }
}
