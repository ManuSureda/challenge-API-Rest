package com.disneymovie.disneyJava.user.services;

import com.disneymovie.disneyJava.exceptions.ElementDoesNotExistException;
import com.disneymovie.disneyJava.user.dto.LoginRequestDto;
import com.disneymovie.disneyJava.user.dto.RegisterRequestDto;
import com.disneymovie.disneyJava.user.model.User;
import com.disneymovie.disneyJava.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(LoginRequestDto login) throws ElementDoesNotExistException {
        User user = userRepository.getByEmail(login.getEmail(), login.getPassword());
        return Optional.ofNullable(user).orElseThrow(() -> new ElementDoesNotExistException("User not exist"));
    }

    public Integer register(RegisterRequestDto registerRequestDto) throws JpaSystemException {
        return userRepository.register(
                registerRequestDto.getUserRoleId(),
                registerRequestDto.getEmail(),
                registerRequestDto.getPassword()
        );
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }
}
