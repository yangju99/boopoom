package com.example.boopoom.web;

import com.example.boopoom.domain.DamageStatus;
import com.example.boopoom.domain.Platform;
import com.example.boopoom.domain.Trade;
import com.example.boopoom.domain.TradeSearch;
import com.example.boopoom.domain.product.Product;
import com.example.boopoom.service.ProductService;
import com.example.boopoom.service.TradeService;
import com.example.boopoom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TradeController {
    private final TradeService tradeService;
    private final UserService userService;
    private final ProductService productService;

    // 거래 등록 화면에 필요한 상품 목록을 조회해 폼을 보여준다.
    @GetMapping("/trades/new")
    public String reportTradeForm(Model model){
        List<Product> products = productService.findProducts();
        model.addAttribute("products", products);
        return "trades/tradeForm";
    }

    // 입력받은 거래 정보를 저장하고 거래 목록 페이지로 이동한다.
    @PostMapping("/trades/new")
    public String createTrade(@RequestParam("userId") Long userId,
                              @RequestParam("productId") Long productId,
                              @RequestParam("price") int price,
                              @RequestParam("location") String location,
                              @RequestParam("platform") Platform platform,
                              @RequestParam("damageStatus") DamageStatus damageStatus){
        tradeService.reportTrade(userId, productId, price, location, platform, damageStatus);
        return "redirect:/trades";
    }

    // 검색 조건에 맞는 거래 목록을 조회해 화면에 표시한다.
    @GetMapping("/trades")
    public String tradeList(@ModelAttribute("tradeSearch") TradeSearch tradeSearch,
                       Model model){
        List<Trade> trades = tradeService.findTrades(tradeSearch);
        model.addAttribute("trades", trades);
        return "trades/tradeList";
    }

    // 선택한 거래를 취소 처리한 뒤 거래 목록 페이지로 이동한다.
    @PostMapping(value="/trades/{tradeId}/cancel")
    public String cancelTrade(@PathVariable("tradeId") Long tradeId){
        tradeService.cancelTrade(tradeId);
        return "redirect:/trades";
    }
}
