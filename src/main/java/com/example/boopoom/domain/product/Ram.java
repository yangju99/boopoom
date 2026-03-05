package com.example.boopoom.domain.product;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("R")
@Getter
@Setter
public class Ram extends Product{
    private int capacityGb;
    private int clockSpeedMhz;
    private int casLatency;

    public static Ram createRam(
            String modelName,
            String modelNumber,
            String brand,
            String generation,
            int capacityGb,
            int clockSpeedMhz,
            int casLatency
    ) {
        Ram ram = new Ram();

        ram.setModelName(modelName);
        ram.setModelNumber(modelNumber);
        ram.setBrand(brand);
        ram.setGeneration(generation);

        ram.setCapacityGb(capacityGb);
        ram.setClockSpeedMhz(clockSpeedMhz);
        ram.setCasLatency(casLatency);

        return ram;
    }

}
