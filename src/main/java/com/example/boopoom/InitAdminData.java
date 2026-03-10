package com.example.boopoom;

import com.example.boopoom.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "boopoom.seed.enabled", havingValue = "false", matchIfMissing = true)
public class InitAdminData implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) {
        String adminEmail = "admin@boopoom.com";
        if (userService.findOneByEmail(adminEmail).isPresent()) {
            return;
        }

        userService.registerAdmin("admin", adminEmail, "1234");
        log.info("초기 관리자 계정을 생성했습니다. email={}", adminEmail);
    }
}
