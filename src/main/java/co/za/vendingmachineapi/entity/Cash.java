package co.za.vendingmachineapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "CASH")
public class Cash {
    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 50)
    private String name;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @Column(name = "DENOMINATION", nullable = false)
    private Integer denomination;

}