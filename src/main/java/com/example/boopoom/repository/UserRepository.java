package com.example.boopoom.repository;

import com.example.boopoom.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(User user){
        em.persist(user);
    }

    public User findOne(Long id){
        return em.find(User.class, id);
    }
    public List<User> findAll(){
        return em.createQuery("select u from User u", User.class).getResultList();
    }

    public List<User> findByNickName(String nickName){
        return em.createQuery("select u from User u where u.nickName = :nickName",
                        User.class)
                .setParameter("nickName", nickName)
                .getResultList();
    }

    public List<User> findByEmail(String email){
        return em.createQuery("select u from User u where u.email = :email",
                        User.class)
                .setParameter("email", email)
                .getResultList();
    }
}
