package com.example.boopoom.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class TradeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id", nullable = false)
    private Trade trade;

    @Column(nullable = false, length = 255)
    private String storageKey;

    @Column(nullable = false, length = 1024)
    private String imageUrl;

    @Column(nullable = false, length = 255)
    private String originalFilename;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false)
    private long sizeBytes;

    @Column(nullable = false)
    private int sortOrder;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public static TradeImage createTradeImage(String storageKey,
                                              String imageUrl,
                                              String originalFilename,
                                              String contentType,
                                              long sizeBytes,
                                              int sortOrder) {
        TradeImage tradeImage = new TradeImage();
        tradeImage.setStorageKey(storageKey);
        tradeImage.setImageUrl(imageUrl);
        tradeImage.setOriginalFilename(originalFilename);
        tradeImage.setContentType(contentType);
        tradeImage.setSizeBytes(sizeBytes);
        tradeImage.setSortOrder(sortOrder);
        return tradeImage;
    }
}
