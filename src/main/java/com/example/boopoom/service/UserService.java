package com.example.boopoom.service;

import com.example.boopoom.domain.User;
import com.example.boopoom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    public Long join(User user)
    {
        validateDuplicateMember(user);
        userRepository.save(user);
        return user.getId();
    }

    private void validateDuplicateMember(User user) {
        List<User> findByEmail = userRepository.findByEmail(user.getEmail());
        List<User> findByNickName = userRepository.findByNickName(user.getNickName());
        if (!findByEmail.isEmpty() || !findByNickName.isEmpty() ){
            throw new IllegalStateException("이미 존재하는 이메일 혹은 닉네임입니다.");
        }
    }

    public List<User> findUsers(){
        return userRepository.findAll();
    }

    public User findOne(Long userId){
        return userRepository.findOne(userId);
    }
}
