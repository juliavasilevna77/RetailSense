package com.university.retail.service.impl;

import com.university.retail.domain.LoginDTO;
import com.university.retail.domain.RegisterDTO;
import com.university.retail.domain.UserDTO;
import com.university.retail.domain.enums.Role;
import com.university.retail.repository.UserRepository;
import com.university.retail.entity.UserEntity;
import com.university.retail.exception.AuthenticationException;
import com.university.retail.exception.UserAlreadyExistsException;
import com.university.retail.security.jwt.JwtProvider;
import com.university.retail.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO registerUser(RegisterDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        UserEntity user = userRepository.save(UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.MANAGER)
                .build());

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }

    @Override
    public UserDTO loginUser(LoginDTO request) {
        Optional<UserEntity> userOpt = Optional.ofNullable(userRepository.findByEmail(request.getEmail()));
        UserEntity user = userOpt.orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.out.println("Password does not match for email: " + request.getEmail());
            throw new AuthenticationException("Invalid email or password");
        }

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }

    public String generateToken(UserDTO user) {
        Role role = userRepository.findByEmail(user.getEmail()).getRole();
        return jwtProvider.createToken(user.getEmail(), role);
    }
}
