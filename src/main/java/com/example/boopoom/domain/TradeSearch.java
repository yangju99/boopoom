package com.example.boopoom.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

//거래 필터링 조건
public class TradeSearch {
    private LocalDateTime startDate;
    private String category; //G, S, R (gpu, ssd, ram)
}
