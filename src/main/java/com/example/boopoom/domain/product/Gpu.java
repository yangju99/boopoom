package com.example.boopoom.domain.product;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("G")
@Getter
@Setter
public class Gpu extends Product{
}
