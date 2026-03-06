package com.example.boopoom.repository;

import com.example.boopoom.domain.product.Product;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepository {
    private final EntityManager em;

    public void save(Product product){
        em.persist(product);
    }

    public Product findOne(Long id){
        return em.find(Product.class, id);
    }

    public List<Product> findAll(){
        return em.createQuery("select p from Product p", Product.class).getResultList();
    }
}
