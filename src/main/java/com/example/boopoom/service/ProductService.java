package com.example.boopoom.service;


import com.example.boopoom.domain.product.Product;
import com.example.boopoom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void saveProduct(Product product){
        productRepository.save(product);
    }

    public List<Product> findProducts() {
        return productRepository.findAll();
    }

    public Product findOne(Long id){
        return productRepository.findOne(id);
    }

    @Transactional
    public void updateProduct(Long productId,
                           String modelName,
                           String modelNumber,
                           int releaseYear,
                           String brand,
                           String generation) {
        Product product = productRepository.findOne(productId);
        product.setModelName(modelName);
        product.setModelNumber(modelNumber);
        product.setReleaseYear(releaseYear);
        product.setBrand(brand);
        product.setGeneration(generation);
    }

}
