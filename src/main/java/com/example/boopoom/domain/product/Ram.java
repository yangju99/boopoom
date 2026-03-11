package com.example.boopoom.domain.product;

import com.example.boopoom.web.forms.product.RamForm;
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

    public static Ram createRam(
            String modelName,
            String modelNumber,
            int releaseYear,
            String brand,
            String generation,
            int capacityGb
    ) {
        Ram ram = new Ram();

        ram.setModelName(modelName);
        ram.setModelNumber(modelNumber);
        ram.setReleaseYear(releaseYear);
        ram.setBrand(brand);
        ram.setGeneration(generation);

        ram.setCapacityGb(capacityGb);

        return ram;
    }
    public static Ram createRam(RamForm ramForm) {
        Ram ram = new Ram();

        ram.setModelName(ramForm.getModelName());
        ram.setModelNumber(ramForm.getModelNumber());
        ram.setReleaseYear(ramForm.getReleaseYear());
        ram.setBrand(ramForm.getBrand());
        ram.setGeneration(ramForm.getGeneration());

        ram.setCapacityGb(ramForm.getCapacityGb());

        return ram;
    }
}
