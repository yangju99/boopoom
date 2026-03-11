package com.example.boopoom.web.forms.product;

import com.example.boopoom.domain.product.Ram;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RamForm extends ProductForm {
    private int capacityGb;

    public static RamForm fromEntity(Ram ram) {
        RamForm form = new RamForm();
        form.setModelName(ram.getModelName());
        form.setModelNumber(ram.getModelNumber());
        form.setReleaseYear(ram.getReleaseYear());
        form.setBrand(ram.getBrand());
        form.setGeneration(ram.getGeneration());
        form.setCapacityGb(ram.getCapacityGb());
        return form;
    }
}