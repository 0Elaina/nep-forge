package com.nep.system.service.impl;

import com.nep.system.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nep.system.dto.RegisterRequest;
import com.nep.system.vo.RegisterVO;

import lombok.RequiredArgsConstructor;

import java.util.Locale;
import com.nep.system.mapper.UserMapper;
import com.nep.system.mapper.RoleMapper;
import com.nep.system.mapper.RoleUserMapper;
import com.nep.system.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nep.common.constants.FieldConstant;
import com.nep.common.exception.CommonException;
import com.nep.common.exception.CommonErrorCode;
import com.nep.system.entity.Role;
import com.nep.common.exception.UserErrorCode;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.nep.system.entity.RoleUser;



@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_ROLE_CODE = "ROLE_USER";

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final RoleUserMapper roleUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterVO register(RegisterRequest request) {
        // 从请求参数中提取用户名、邮箱和密码
        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        // 检查用户名和邮箱是否存在
        checkUsernameNotExists(username);
        checkEmailNotExists(email);

        Role defaultRole = roleMapper.selectOne(
            new LambdaQueryWrapper<Role>()
            .eq(Role::getRoleCode, DEFAULT_ROLE_CODE)
            .eq(Role::getIsDeleted, FieldConstant.NOT_DELETED)
            .last("limit 1")
        );

        if (defaultRole == null) {
            throw new CommonException(CommonErrorCode.SYSTEM_ERROR,
                "默认角色 ROLE_USER 不存在，请检查数据库初始化脚本"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        // 创建并保存用户实体
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setAvatar(null);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setIsDeleted(FieldConstant.NOT_DELETED);
        user.setNickname(null);
        user.setBio(null);
        user.setStatus(FieldConstant.ENABLED);
        user.setLastLoginTime(now);
        userMapper.insert(user);

        RoleUser roleUser = new RoleUser();
        roleUser.setUserId(user.getId());
        roleUser.setRoleId(defaultRole.getId());
        roleUser.setCreateTime(now);
        roleUser.setUpdateTime(now);

        roleUserMapper.insert(roleUser);

        return new RegisterVO(
            String.valueOf(user.getId()),
            user.getUsername(),
            user.getEmail()
        );
    }

    /**
     * 检查用户名是否存在
     * 如果用户名存在，则抛出异常
     * @param username 用户名
     * @throws CommonException 如果用户名存在
     */
    private void checkUsernameNotExists(String username) {
        Long count = userMapper.selectCount(
            new LambdaQueryWrapper<User>()
            .eq(User::getUsername, username)
            .eq(User::getIsDeleted, FieldConstant.NOT_DELETED)
            .eq(User::getStatus, FieldConstant.ENABLED)
        );

        if (count != null && count > 0) {
            throw new CommonException(UserErrorCode.USERNAME_EXISTS);
        }
    }

    /**
     * 检查邮箱是否存在
     * 如果邮箱存在，则抛出异常
     * @param email 邮箱
     * @throws CommonException 如果邮箱存在
     */
    private void checkEmailNotExists(String email) {
        Long count = userMapper.selectCount(
            new LambdaQueryWrapper<User>()
            .eq(User::getEmail, email)
            .eq(User::getIsDeleted, FieldConstant.NOT_DELETED)
            .eq(User::getStatus, FieldConstant.ENABLED)
        );

        if (count != null && count > 0) {
            throw new CommonException(UserErrorCode.EMAIL_EXISTS);
        }
    }
}
