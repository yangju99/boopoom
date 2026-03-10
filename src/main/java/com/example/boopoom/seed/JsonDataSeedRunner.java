package com.example.boopoom.seed;

import com.example.boopoom.domain.DamageStatus;
import com.example.boopoom.domain.Platform;
import com.example.boopoom.domain.Role;
import com.example.boopoom.domain.Trade;
import com.example.boopoom.domain.TradeStatus;
import com.example.boopoom.domain.User;
import com.example.boopoom.domain.product.Gpu;
import com.example.boopoom.domain.product.Product;
import com.example.boopoom.domain.product.Ram;
import com.example.boopoom.domain.product.Ssd;
import com.example.boopoom.repository.ProductRepository;
import com.example.boopoom.repository.TradeRepository;
import com.example.boopoom.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Order(100)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "boopoom.seed.enabled", havingValue = "true")
public class JsonDataSeedRunner implements CommandLineRunner {

    // Keep JSON loading self-contained so seeding does not depend on auto-configured web beans.
    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final TradeRepository tradeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager em;
    private final ConfigurableApplicationContext context;
    private final PlatformTransactionManager transactionManager;

    @org.springframework.beans.factory.annotation.Value("${boopoom.seed.force:false}")
    private boolean forceSeed;

    @org.springframework.beans.factory.annotation.Value("${boopoom.seed.exit:true}")
    private boolean exitAfterSeed;

    @Override
    public void run(String... args) throws Exception {
        SeedResult result = new TransactionTemplate(transactionManager).execute(status -> {
            try {
                if (!forceSeed && hasAnyData()) {
                    return SeedResult.skippedResult();
                }

                if (forceSeed) {
                    clearAllData();
                }

                Map<String, Product> productMap = seedProducts();
                Map<String, User> userMap = seedUsers();
                seedTrades(userMap, productMap);

                return SeedResult.completedResult(count("Product"), count("User"), count("Trade"));
            } catch (IOException e) {
                throw new IllegalStateException("Failed to load seed data from JSON.", e);
            }
        });

        if (result == null) {
            throw new IllegalStateException("Seed transaction did not produce a result.");
        }

        if (result.skipped()) {
            log.info("Seed skipped: data already exists. Use --boopoom.seed.force=true to reseed.");
        } else {
            log.info("Seed completed. products={}, users={}, trades={}",
                    result.productCount(), result.userCount(), result.tradeCount());
        }
        closeIfRequested();
    }

    private boolean hasAnyData() {
        return count("Product") > 0 || count("User") > 0 || count("Trade") > 0;
    }

    private void clearAllData() {
        em.createQuery("delete from Trade").executeUpdate();
        em.createQuery("delete from Product").executeUpdate();
        em.createQuery("delete from User").executeUpdate();
        em.flush();
    }

    private long count(String entityName) {
        return em.createQuery("select count(e) from " + entityName + " e", Long.class).getSingleResult();
    }

    private void closeIfRequested() {
        if (exitAfterSeed) {
            context.close();
        }
    }

    private Map<String, Product> seedProducts() throws IOException {
        Map<String, Product> productMap = new HashMap<>();

        for (GpuSeed seed : readList("data/product/gpu.json", new TypeReference<List<GpuSeed>>() {})) {
            Gpu gpu = Gpu.createGpu(seed.modelName(), seed.modelNumber(), seed.releaseYear(), seed.brand(), seed.generation(),
                    seed.vramGb(), seed.clockSpeedMhz(), seed.powerRequirementW());
            productRepository.save(gpu);
            productMap.put(seed.code(), gpu);
        }

        for (SsdSeed seed : readList("data/product/ssd.json", new TypeReference<List<SsdSeed>>() {})) {
            Ssd ssd = Ssd.createSsd(seed.modelName(), seed.modelNumber(), seed.releaseYear(), seed.brand(), seed.generation(),
                    seed.capacityGb());
            productRepository.save(ssd);
            productMap.put(seed.code(), ssd);
        }

        for (RamSeed seed : readList("data/product/ram.json", new TypeReference<List<RamSeed>>() {})) {
            Ram ram = Ram.createRam(seed.modelName(), seed.modelNumber(), seed.releaseYear(), seed.brand(), seed.generation(),
                    seed.capacityGb(), seed.clockSpeedMhz(), seed.casLatency());
            productRepository.save(ram);
            productMap.put(seed.code(), ram);
        }

        return productMap;
    }

    private Map<String, User> seedUsers() throws IOException {
        Map<String, User> userMap = new HashMap<>();
        List<UserSeed> userSeeds = readList("data/user/users.json", new TypeReference<List<UserSeed>>() {});

        for (UserSeed seed : userSeeds) {
            User user = userRepository.findOneByEmail(seed.email()).orElse(null);
            if (user == null) {
                String encoded = passwordEncoder.encode(seed.password());
                user = Role.ADMIN.name().equalsIgnoreCase(seed.role())
                        ? User.createAdmin(seed.nickName(), seed.email(), encoded)
                        : User.createUser(seed.nickName(), seed.email(), encoded);
                if (seed.points() != null) {
                    user.setPoints(seed.points());
                }
                userRepository.save(user);
            }
            userMap.put(seed.email(), user);
        }
        return userMap;
    }

    private void seedTrades(Map<String, User> userMap, Map<String, Product> productMap) throws IOException {
        List<TradeSeed> tradeSeeds = readList("data/trade/trades.json", new TypeReference<List<TradeSeed>>() {});

        for (TradeSeed seed : tradeSeeds) {
            User user = userMap.get(seed.userEmail());
            Product product = productMap.get(seed.productCode());
            if (user == null || product == null) {
                continue;
            }

            Trade trade = Trade.createTrade(
                    user,
                    product,
                    seed.price(),
                    seed.location(),
                    Platform.valueOf(seed.platform()),
                    DamageStatus.valueOf(seed.damageStatus()),
                    LocalDate.parse(seed.tradeDate())
            );

            if (seed.status() != null && !seed.status().isBlank()) {
                TradeStatus status = TradeStatus.valueOf(seed.status());
                if (status == TradeStatus.COMPLETED) {
                    trade.complete();
                } else if (status == TradeStatus.FAILED) {
                    trade.cancel();
                }
            }
            tradeRepository.save(trade);
        }
    }

    private <T> List<T> readList(String path, TypeReference<List<T>> type) throws IOException {
        return objectMapper.readValue(Path.of(path).toFile(), type);
    }

    private record GpuSeed(
            String code,
            String modelName,
            String modelNumber,
            int releaseYear,
            String brand,
            String generation,
            int vramGb,
            int clockSpeedMhz,
            int powerRequirementW
    ) {}

    private record SsdSeed(
            String code,
            String modelName,
            String modelNumber,
            int releaseYear,
            String brand,
            String generation,
            int capacityGb
    ) {}

    private record RamSeed(
            String code,
            String modelName,
            String modelNumber,
            int releaseYear,
            String brand,
            String generation,
            int capacityGb,
            int clockSpeedMhz,
            int casLatency
    ) {}

    private record UserSeed(
            String nickName,
            String email,
            String password,
            String role,
            Integer points
    ) {}

    private record TradeSeed(
            String userEmail,
            String productCode,
            int price,
            String location,
            String platform,
            String damageStatus,
            String tradeDate,
            String status
    ) {}

    private record SeedResult(boolean skipped, long productCount, long userCount, long tradeCount) {

        private static SeedResult skippedResult() {
            return new SeedResult(true, 0, 0, 0);
        }

        private static SeedResult completedResult(long productCount, long userCount, long tradeCount) {
            return new SeedResult(false, productCount, userCount, tradeCount);
        }
    }
}
