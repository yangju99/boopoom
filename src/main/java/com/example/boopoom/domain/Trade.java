package com.example.boopoom.domain;

import com.example.boopoom.domain.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    private String location;

    private int price;

    private LocalDate tradeDate;

    @Enumerated(EnumType.STRING)
    private TradeStatus status;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    // product(gpu, cpu, ssd) 공통 상태 ex) 파손 여부
    @Enumerated(EnumType.STRING)
    private DamageStatus damageStatus;

    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TradeImage> images = new ArrayList<>();

    public void setUser(User user){
        this.user = user;
        user.addTrade(this);
    }

    public void setProduct (Product product){
        this.product = product;
        product.addTrade(this);
    }

    public void addImage(TradeImage image) {
        images.add(image);
        image.setTrade(this);
    }

    public static Trade createTrade(User user,
                                    Product product,
                                    int price,
                                    String location,
                                    Platform platform,
                                    DamageStatus damageStatus,
                                    LocalDate tradeDate){
        Trade trade = new Trade();
        trade.setUser(user);
        trade.setProduct(product);

        trade.setPrice(price);
        trade.setLocation(location);
        trade.setPlatform(platform);
        trade.setDamageStatus(damageStatus);

        //처음 등록할때 pending 상태로 등록
        trade.setStatus(TradeStatus.PENDING);
        trade.setTradeDate(tradeDate);

        return trade;

    }

    //trade 실패시 취소기능
    public void cancel(){
        if (this.getStatus() == TradeStatus.COMPLETED){
            throw new IllegalStateException("이미 거래완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(TradeStatus.FAILED);
    }

    //trade 성사시 complete로 상태 변경
    public void complete(){
        this.status = TradeStatus.COMPLETED;
    }
}
