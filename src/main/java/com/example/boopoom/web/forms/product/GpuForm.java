package com.example.boopoom.web.forms.product;

import com.example.boopoom.domain.product.Gpu;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GpuForm extends ProductForm{
    private int vramGb;
    private int powerRequirementW;

    public static GpuForm fromEntity(Gpu gpu) {
        GpuForm form = new GpuForm();
        form.setModelName(gpu.getModelName());
        form.setModelNumber(gpu.getModelNumber());
        form.setReleaseYear(gpu.getReleaseYear());
        form.setBrand(gpu.getBrand());
        form.setGeneration(gpu.getGeneration());
        form.setVramGb(gpu.getVramGb());
        form.setPowerRequirementW(gpu.getPowerRequirementW());
        return form;
    }
}
