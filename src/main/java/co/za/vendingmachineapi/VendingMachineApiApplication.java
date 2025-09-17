package co.za.vendingmachineapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VendingMachineApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VendingMachineApiApplication.class, args);
    }

}
