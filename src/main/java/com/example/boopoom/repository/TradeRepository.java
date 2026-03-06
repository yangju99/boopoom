package com.example.boopoom.repository;

import com.example.boopoom.domain.Trade;
import com.example.boopoom.domain.TradeSearch;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TradeRepository {

    private final EntityManager em;


    public void save(Trade trade){
        em.persist(trade);
    }

    public Trade findOne(Long id){
        return em.find(Trade.class, id);
    }

    public List<Trade> findAll(TradeSearch tradeSearch){
        String jpql = "select t From Trade t join t.product p";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (tradeSearch.getStartDate() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " t.tradeDate > :startDate";
        }
        //회원 이름 검색
        if (StringUtils.hasText(tradeSearch.getCategory())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " p.dtype = :category";
        }
        TypedQuery<Trade> query = em.createQuery(jpql, Trade.class)
                .setMaxResults(100); //최대 1000건
        if (tradeSearch.getStartDate() != null) {
            query = query.setParameter("startDate", tradeSearch.getStartDate());
        }
        if (StringUtils.hasText(tradeSearch.getCategory())) {
            query = query.setParameter("category", tradeSearch.getCategory());
        }
        return query.getResultList();
    }
}
