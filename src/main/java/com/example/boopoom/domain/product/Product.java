package com.example.boopoom.domain.product;

import com.example.boopoom.domain.Trade;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="dtype")
@Getter
@Setter
public abstract class Product {
    @Id
    @GeneratedValue
    @Column(name="item_id")
    private Long id;

    private String modelName;
    private String modelNumber;

    private int releaseYear;
    private String brand; //SAMSUNG, NVIDIA
    private String generation;

    @OneToMany(mappedBy="product")
    private List<Trade> trades = new ArrayList<>();

    public void addTrade(Trade trade) {
        trades.add(trade);
    }

    public String getCategory() {
        if (this instanceof Gpu) return "G"; // Gpu의 DiscriminatorValue
        if (this instanceof Ram) return "R";
        if (this instanceof Ssd) return "S";
        return "UNKNOWN";
    }

    public String getCategoryDisplay() {
        if (this instanceof Gpu) return "GPU";
        if (this instanceof Ram) return "RAM";
        if (this instanceof Ssd) return "SSD";
        return "UNKNOWN";
    }
}
