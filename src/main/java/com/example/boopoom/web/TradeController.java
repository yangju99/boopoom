package com.example.boopoom.web;

import com.example.boopoom.domain.DamageStatus;
import com.example.boopoom.domain.Platform;
import com.example.boopoom.domain.Trade;
import com.example.boopoom.domain.TradeSearch;
import com.example.boopoom.domain.TradeImage;
import com.example.boopoom.domain.product.Product;
import com.example.boopoom.exception.NotEnoughPointsException;
import com.example.boopoom.service.ProductService;
import com.example.boopoom.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

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
                              @RequestParam(value = "images", required = false) List<MultipartFile> images,
                              Principal principal){
        tradeService.reportTrade(principal.getName(), productId, price, location, platform, damageStatus, tradeDate, images);
        return "redirect:/";
    }

    // 유저는 검색 시 포인트를 사용하여 거래 목록을 조회한다.
    @GetMapping("/trades")
    public String tradeSearchForm(@ModelAttribute("tradeSearch") TradeSearch tradeSearch,
                                  Model model){
        setUserSearchActions(model);
        return "trades/tradeSearchForm";
    }

    // 유저가 조회 버튼을 눌렀을 때 포인트를 사용하여 거래 목록을 조회한다.
    @PostMapping("/trades/search")
    public String tradeList(@ModelAttribute("tradeSearch") TradeSearch tradeSearch,
                            Principal principal,
                            Model model){
        try {
            List<Trade> trades = tradeService.findTradesForUser(tradeSearch, principal.getName());
            model.addAttribute("trades", trades);
            return "trades/tradeList";
        } catch (NotEnoughPointsException e) {
            setUserSearchActions(model);
            model.addAttribute("pointErrorMessage", "포인트가 부족하여 거래 조회를 진행할 수 없습니다.");
            return "trades/tradeSearchForm";
        }
    }

    // 유저가 조회 버튼을 눌렀을 때 포인트를 사용하여 거래 산점도를 조회한다.
    @PostMapping("/trades/scatter")
    public String tradeScatter(@ModelAttribute("tradeSearch") TradeSearch tradeSearch,
                               Principal principal,
                               Model model) {
        try {
            List<Trade> trades = tradeService.findTradesForUser(tradeSearch, principal.getName());
            model.addAttribute("scatterPoints", toScatterPoints(trades));
            model.addAttribute("listAction", "/trades/search");
            model.addAttribute("conditionUrl", "/trades");
            return "trades/tradeScatter";
        } catch (NotEnoughPointsException e) {
            setUserSearchActions(model);
            model.addAttribute("pointErrorMessage", "포인트가 부족하여 산점도 조회를 진행할 수 없습니다.");
            return "trades/tradeSearchForm";
        }
    }

    // 관리자는 포인트 차감 없이 거래 목록을 조회한다.
    @GetMapping("/admin/trades")
    public String adminTradeSearchForm(@ModelAttribute("tradeSearch") TradeSearch tradeSearch,
                                       Model model){
        model.addAttribute("searchAction", "/admin/trades/search");
        model.addAttribute("scatterAction", "/admin/trades/scatter");
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

    // 관리자가 조회 버튼을 눌렀을 때 거래 산점도를 조회한다.
    @PostMapping("/admin/trades/scatter")
    public String adminTradeScatter(@ModelAttribute("tradeSearch") TradeSearch tradeSearch,
                                    Model model) {
        List<Trade> trades = tradeService.findTradesForAdmin(tradeSearch);
        model.addAttribute("scatterPoints", toScatterPoints(trades));
        model.addAttribute("listAction", "/admin/trades/search");
        model.addAttribute("conditionUrl", "/admin/trades");
        return "trades/tradeScatter";
    }

    // 선택한 거래를 취소 처리한 뒤 거래 목록 페이지로 이동한다.
    @PostMapping(value="/trades/{tradeId}/cancel")
    public String cancelTrade(@PathVariable("tradeId") Long tradeId){
        tradeService.cancelTrade(tradeId);
        return "redirect:/trades";
    }

    private List<ScatterTradePoint> toScatterPoints(List<Trade> trades) {
        ZoneId zoneId = ZoneId.systemDefault();
        return trades.stream()
                .map(trade -> new ScatterTradePoint(
                        trade.getId(),
                        trade.getTradeDate().toString(),
                        trade.getTradeDate().atStartOfDay(zoneId).toInstant().toEpochMilli(),
                        trade.getPrice(),
                        trade.getProduct().getModelName(),
                        trade.getProduct().getCategoryDisplay(),
                        trade.getPlatform().name(),
                        trade.getStatus().name(),
                        trade.getLocation(),
                        trade.getDamageStatus().name(),
                        trade.getImages().stream()
                                .map(TradeImage::getImageUrl)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    private void setUserSearchActions(Model model) {
        model.addAttribute("searchAction", "/trades/search");
        model.addAttribute("scatterAction", "/trades/scatter");
    }

    private record ScatterTradePoint(
            Long id,
            String tradeDate,
            long tradeDateMs,
            int price,
            String productName,
            String category,
            String platform,
            String status,
            String location,
            String damageStatus,
            List<String> imageUrls
    ) {
    }
}
