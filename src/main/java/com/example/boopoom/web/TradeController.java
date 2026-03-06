package com.example.boopoom.web;

import com.example.boopoom.domain.DamageStatus;
import com.example.boopoom.domain.Platform;
import com.example.boopoom.domain.Trade;
import com.example.boopoom.domain.TradeSearch;
import com.example.boopoom.domain.product.Product;
import com.example.boopoom.service.ProductService;
import com.example.boopoom.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TradeController {
    private final TradeService tradeService;
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
    public String createTrade(@RequestParam("productId") Long productId,
                              @RequestParam("price") int price,
                              @RequestParam("location") String location,
                              @RequestParam("platform") Platform platform,
                              @RequestParam("damageStatus") DamageStatus damageStatus,
                              @RequestParam("tradeDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tradeDate,
                              Principal principal){
        tradeService.reportTrade(principal.getName(), productId, price, location, platform, damageStatus, tradeDate);
        return "redirect:/";
    }

    // 유저는 검색 시 포인트를 사용하여 거래 목록을 조회한다.
    @GetMapping("/trades")
    public String tradeSearchForm(@ModelAttribute("tradeSearch") TradeSearch tradeSearch,
                                  Model model){
        model.addAttribute("searchAction", "/trades/search");
        return "trades/tradeSearchForm";
    }

    // 유저가 조회 버튼을 눌렀을 때 포인트를 사용하여 거래 목록을 조회한다.
    @PostMapping("/trades/search")
    public String tradeList(@ModelAttribute("tradeSearch") TradeSearch tradeSearch,
                            Principal principal,
                            Model model){
        List<Trade> trades = tradeService.findTradesForUser(tradeSearch, principal.getName());
        model.addAttribute("trades", trades);
        return "trades/tradeList";
    }

    // 관리자는 포인트 차감 없이 거래 목록을 조회한다.
    @GetMapping("/admin/trades")
    public String adminTradeSearchForm(@ModelAttribute("tradeSearch") TradeSearch tradeSearch,
                                       Model model){
        model.addAttribute("searchAction", "/admin/trades/search");
        return "trades/tradeSearchForm";
    }

    // 관리자가 조회 버튼을 눌렀을 때 거래 목록을 조회한다.
    @PostMapping("/admin/trades/search")
    public String adminTradeList(@ModelAttribute("tradeSearch") TradeSearch tradeSearch,
                                 Model model){
        List<Trade> trades = tradeService.findTradesForAdmin(tradeSearch);
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
