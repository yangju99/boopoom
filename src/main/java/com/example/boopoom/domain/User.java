package com.example.boopoom.domain;

import com.example.boopoom.exception.NotEnoughPointsException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private int points;
    private static final int POINT_AMOUNT = 100;
    private static final int INITIAL_POINT = 500;

    @OneToMany(mappedBy="user")
    private List<Trade> trades = new ArrayList<>();

    public void addTrade(Trade trade){
        this.trades.add(trade);
    }

    public void getPoints(){
        this.points += User.POINT_AMOUNT;
    }

    public void usePoints(){
        int restPoints = this.points - User.POINT_AMOUNT;
        if (restPoints<0){
            throw new NotEnoughPointsException("need more points");
        }
        this.points = restPoints;
    }

    public static User createUser(String nickName, String email, String password){
        User user = new User();
        user.setNickName(nickName);
        user.setEmail(email);
        user.setPoints(User.INITIAL_POINT);
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(Role.USER);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPasswordHash(passwordEncoder.encode(password));

        return user;
    }

    public static User createAdmin(String nickName, String email, String password) {
        User admin = createUser(nickName, email, password);
        admin.setRole(Role.ADMIN);
        return admin;
    }
}
