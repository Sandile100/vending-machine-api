package co.za.vendingmachineapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "SALES")
public class Sale {
    @Id()
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "DATEADDED")
    private Instant dateadded;

    @Column(name = "PRICE", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "PRODUCTNAME", nullable = false, length = 50)
    private String productname;

    @Column(name = "PRODUCTID", nullable = false)
    private Integer productid;

}