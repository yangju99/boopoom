package com.example.boopoom.service;

import com.example.boopoom.domain.User;
import com.example.boopoom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Long registerUser(String nickName, String email, String rawPassword) {
        String encodedPasswordHash = passwordEncoder.encode(rawPassword);
        User user = User.createUser(nickName, email, encodedPasswordHash);
        validateDuplicateMember(user);
        userRepository.save(user);
        return user.getId();
    }

    @Transactional
    public Long registerAdmin(String nickName, String email, String rawPassword) {
        String encodedPasswordHash = passwordEncoder.encode(rawPassword);
        User admin = User.createAdmin(nickName, email, encodedPasswordHash);
        validateDuplicateMember(admin);
        userRepository.save(admin);
        return admin.getId();
    }

    private void validateDuplicateMember(User user) {
        Optional<User> findByEmail = userRepository.findOneByEmail(user.getEmail());
        Optional<User> findByNickName = userRepository.findOneByNickName(user.getNickName());

        if (findByEmail.isPresent() || findByNickName.isPresent()) {
            throw new IllegalStateException("이미 존재하는 이메일 혹은 닉네임입니다.");
        }
    }
    public List<User> findUsers(){
        return userRepository.findAll();
    }

    public User findOne(Long userId){
        return userRepository.findOne(userId);
    }

    public Optional<User> findOneByEmail(String email) {
        return userRepository.findOneByEmail(email);
    }

    public Optional<User> findOneByNickName(String nickName) {
        return userRepository.findOneByNickName(nickName);
    }
}
