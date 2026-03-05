package com.example.boopoom.domain;

import com.example.boopoom.exception.NotEnoughPointsException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    private String name;
    private String email;
    private String password;

    private int points;
    private static final int POINT_AMOUNT = 100;

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
}
