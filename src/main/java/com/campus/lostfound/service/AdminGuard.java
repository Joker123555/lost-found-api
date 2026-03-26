package com.campus.lostfound.service;

import com.campus.lostfound.entity.User;
import com.campus.lostfound.exception.BusinessException;
import com.campus.lostfound.repository.UserRepository;
import com.campus.lostfound.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminGuard {

    private final UserRepository userRepository;

    public void requireAdmin() {
        Long id = UserContext.getUserId();
        if (id == null) throw new BusinessException(401, "未登录");
        User u = userRepository.findById(id).orElseThrow(() -> new BusinessException(401, "无效用户"));
        if (u.getRole() == null || u.getRole() != 1) {
            throw new BusinessException(403, "需要管理员权限");
        }
    }
}
