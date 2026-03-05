package com.example.boopoom.domain.product;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("S")
@Getter
@Setter
public class Ssd extends Product {
    private int capacityGb;

    public static Ssd createSsd(
            String modelName,
            String modelNumber,
            String brand,
            String generation,
            int capacityGb
    ) {
        Ssd ssd = new Ssd();

        ssd.setModelName(modelName);
        ssd.setModelNumber(modelNumber);
        ssd.setBrand(brand);
        ssd.setGeneration(generation);

        ssd.setCapacityGb(capacityGb);

        return ssd;
    }

}
