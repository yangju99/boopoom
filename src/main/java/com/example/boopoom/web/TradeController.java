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

    @GetMapping("/trades/new")
    public String reportTradeForm(Model model){
        List<Product> products = productService.findProducts();
        model.addAttribute("products", products);
        return "trades/tradeForm";
    }

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

    //관리자용 (trade 조회해도 point 안깎임)
    @GetMapping("/trades")
    public String tradeList(@ModelAttribute("tradeSearch") TradeSearch tradeSearch,
                       Model model){
        List<Trade> trades = tradeService.findTrades(tradeSearch);
        model.addAttribute("trades", trades);
        return "trades/tradeList";
    }

    @PostMapping(value="/trades/{tradeId}/cancel")
    public String cancelTrade(@PathVariable("tradeId") Long tradeId){
        tradeService.cancelTrade(tradeId);
        return "redirect:/trades";
    }
}
