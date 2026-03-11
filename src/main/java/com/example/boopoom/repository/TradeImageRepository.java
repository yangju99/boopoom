package com.example.boopoom.repository;

import com.example.boopoom.domain.TradeImage;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TradeImageRepository {

    private final EntityManager em;

    public void save(TradeImage tradeImage) {
        em.persist(tradeImage);
    }

    public List<TradeImage> findByTradeId(Long tradeId) {
        return em.createQuery(
                        "select ti from TradeImage ti where ti.trade.id = :tradeId order by ti.sortOrder",
                        TradeImage.class)
                .setParameter("tradeId", tradeId)
                .getResultList();
    }
}
