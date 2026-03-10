package com.example.boopoom.service;

import com.example.boopoom.domain.DamageStatus;
import com.example.boopoom.domain.Platform;
import com.example.boopoom.domain.Trade;
import com.example.boopoom.domain.TradeImage;
import com.example.boopoom.domain.TradeSearch;
import com.example.boopoom.domain.User;
import com.example.boopoom.domain.product.Product;
import com.example.boopoom.repository.ProductRepository;
import com.example.boopoom.repository.TradeImageRepository;
import com.example.boopoom.repository.TradeRepository;
import com.example.boopoom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeService {
    private final TradeRepository tradeRepository;
    private final TradeImageRepository tradeImageRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TradeImageStorageService tradeImageStorageService;


    // user의
    @Transactional
    public Long reportTrade(String userEmail,
                            Long productId,
                            int price,
                            String location,
                            Platform platform,
                            DamageStatus damageStatus,
                            LocalDate tradeDate,
                            List<MultipartFile> images) {

        User user = userRepository.findOneByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));
        Product product = productRepository.findOne(productId);

        Trade trade = Trade.createTrade(user, product, price, location, platform, damageStatus, tradeDate);
        tradeRepository.save(trade);
        saveTradeImages(trade, images);

        user.getPoints(); //제보한 유저 포인트 증가

        return trade.getId();
    }

    private void saveTradeImages(Trade trade, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return;
        }

        List<TradeImageStorageService.StoredImage> storedImages = new ArrayList<>();
        try {
            int sortOrder = 0;
            for (MultipartFile image : images) {
                if (image == null || image.isEmpty()) {
                    continue;
                }
                TradeImageStorageService.StoredImage stored = tradeImageStorageService.store(image);
                storedImages.add(stored);

                TradeImage tradeImage = TradeImage.createTradeImage(
                        stored.storageKey(),
                        stored.imageUrl(),
                        stored.originalFilename(),
                        stored.contentType(),
                        stored.sizeBytes(),
                        sortOrder++
                );
                trade.addImage(tradeImage);
                tradeImageRepository.save(tradeImage);
            }
        } catch (RuntimeException e) {
            for (TradeImageStorageService.StoredImage storedImage : storedImages) {
                tradeImageStorageService.delete(storedImage.storageKey());
            }
            throw e;
        }
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
