package com.example.boopoom.domain.product;

import com.example.boopoom.web.forms.product.SsdForm;
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
    public static Ssd createSsd(SsdForm ssdForm) {
        Ssd ssd = new Ssd();

        ssd.setModelName(ssdForm.getModelName());
        ssd.setModelNumber(ssdForm.getModelNumber());
        ssd.setReleaseYear(ssdForm.getReleaseYear());
        ssd.setBrand(ssdForm.getBrand());
        ssd.setGeneration(ssdForm.getGeneration());

        ssd.setCapacityGb(ssdForm.getCapacityGb());

        return ssd;
    }
}
