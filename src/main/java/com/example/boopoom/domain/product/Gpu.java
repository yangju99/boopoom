package com.example.boopoom.domain.product;

import com.example.boopoom.web.forms.product.GpuForm;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("G")
@Getter
@Setter
public class Gpu extends Product{
    private int vramGb;
    private int clockSpeedMhz;
    private int powerRequirementW;

    public static Gpu createGpu(
            String modelName,
            String modelNumber,
            String brand,
            String generation,
            int vramGb,
            int clockSpeedMhz,
            int powerRequirementW
    ) {
        Gpu gpu = new Gpu();

        gpu.setModelName(modelName);
        gpu.setModelNumber(modelNumber);
        gpu.setBrand(brand);
        gpu.setGeneration(generation);

        gpu.setVramGb(vramGb);
        gpu.setClockSpeedMhz(clockSpeedMhz);
        gpu.setPowerRequirementW(powerRequirementW);

        return gpu;
    }
    public static Gpu createGpu(GpuForm gpuForm) {
        Gpu gpu = new Gpu();
        gpu.setModelName(gpuForm.getModelName());
        gpu.setModelNumber(gpuForm.getModelNumber());
        gpu.setBrand(gpuForm.getBrand());
        gpu.setGeneration(gpuForm.getGeneration());

        gpu.setVramGb(gpuForm.getVramGb());
        gpu.setClockSpeedMhz(gpuForm.getClockSpeedMhz());
        gpu.setPowerRequirementW(gpuForm.getPowerRequirementW());

        return gpu;
    }
}
