package com.campus.lostfound.service;

import com.campus.lostfound.dto.LoginRequest;
import com.campus.lostfound.dto.TokenResponse;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.exception.BusinessException;
import com.campus.lostfound.repository.UserRepository;
import com.campus.lostfound.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public TokenResponse login(LoginRequest req) {
        User u = userRepository.findByAccountAndIsDeleted(req.getAccount().trim(), 0)
                .orElseThrow(() -> new BusinessException("账号或密码错误"));
        if (u.getRole() == null || u.getRole() != 1) {
            throw new BusinessException("非管理员账号");
        }
        if (u.getStatus() != null && u.getStatus() == 1) {
            throw new BusinessException("账号已封禁");
        }
        if (u.getLockedUntil() != null && u.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new BusinessException("账号已锁定");
        }
        if (u.getPassword() == null || !passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        String token = jwtService.createAdminToken(u.getId());
        return new TokenResponse(token, u.getId(), u.getNickname());
    }
}
