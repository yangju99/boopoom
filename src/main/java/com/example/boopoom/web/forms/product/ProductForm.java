package com.example.boopoom.web.forms.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {

    private String modelName;
    private String modelNumber;

    private int releaseYear;
    private String brand; //SAMSUNG, NVIDIA
    private String generation;
}
