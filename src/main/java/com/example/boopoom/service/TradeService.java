package com.example.boopoom.service;

import com.example.boopoom.domain.*;
import com.example.boopoom.domain.product.Product;
import com.example.boopoom.repository.ProductRepository;
import com.example.boopoom.repository.TradeRepository;
import com.example.boopoom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeService {
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    // user의
    @Transactional
    public Long reportTrade(String userEmail,
                            Long productId,
                            int price,
                            String location,
                            Platform platform,
                            DamageStatus damageStatus) {

        User user = userRepository.findOneByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));
        Product product = productRepository.findOne(productId);

        Trade trade = Trade.createTrade(user, product, price, location, platform, damageStatus);

        tradeRepository.save(trade);

        user.getPoints(); //제보한 유저 포인트 증가

        return trade.getId();
    }

    @Transactional
    public void cancelTrade(Long tradeId){
        Trade trade= tradeRepository.findOne(tradeId);
        trade.cancel();
    }

    @Transactional
    public void completeTrade(Long tradeId){
        Trade trade= tradeRepository.findOne(tradeId);
        trade.complete();
    }

    //관리자용 trade 조회
    public List<Trade> findTradesForAdmin(TradeSearch tradeSearch) {
        return tradeRepository.findAll(tradeSearch);
    }

    //유저용 trade 조회 (포인트 사용)
    @Transactional
    public List<Trade> findTradesForUser(TradeSearch tradeSearch, String userEmail) {
        User user = userRepository.findOneByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));
        user.usePoints();
        return tradeRepository.findAll(tradeSearch);
    }
}
