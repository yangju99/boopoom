package com.example.boopoom.web.forms.product;

import com.example.boopoom.domain.product.Ssd;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SsdForm extends ProductForm{
    private int capacityGb;

    public static SsdForm fromEntity(Ssd ssd) {
        SsdForm form = new SsdForm();
        form.setModelName(ssd.getModelName());
        form.setModelNumber(ssd.getModelNumber());
        form.setReleaseYear(ssd.getReleaseYear());
        form.setBrand(ssd.getBrand());
        form.setGeneration(ssd.getGeneration());
        form.setCapacityGb(ssd.getCapacityGb());
        return form;
    }
}
