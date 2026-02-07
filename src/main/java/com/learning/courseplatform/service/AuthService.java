package com.learning.courseplatform.service;

import com.learning.courseplatform.dto.AuthRequest;
import com.learning.courseplatform.dto.AuthResponse;
import com.learning.courseplatform.dto.RegisterResponse;
import com.learning.courseplatform.entity.User;
import com.learning.courseplatform.exception.ConflictException;
import com.learning.courseplatform.exception.UnauthorizedException;
import com.learning.courseplatform.repository.UserRepository;
import com.learning.courseplatform.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public RegisterResponse register(AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        User savedUser = userRepository.save(user);

        return new RegisterResponse(
            savedUser.getId(),
            savedUser.getEmail(),
            "User registered successfully"
        );
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(
            token,
            user.getEmail(),
            jwtUtil.getExpirationTime()
        );
    }
}
