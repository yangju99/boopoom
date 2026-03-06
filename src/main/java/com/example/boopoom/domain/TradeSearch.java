package com.example.boopoom.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter

//거래 필터링 조건
public class TradeSearch {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    private String category; //G, S, R (gpu, ssd, ram)
}
