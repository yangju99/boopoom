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

    private String nickName;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
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

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPasswordHash(passwordEncoder.encode(password));

        return user;
    }
}
