package com.nep.system.service.impl;

import com.nep.system.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nep.system.dto.RegisterRequest;
import com.nep.system.vo.RegisterVO;

import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Locale;
import com.nep.system.mapper.UserMapper;
import com.nep.system.mapper.RoleMapper;
import com.nep.system.mapper.RoleUserMapper;
import com.nep.system.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nep.common.constants.AuthConstant;
import com.nep.common.constants.FieldConstant;
import com.nep.common.exception.CommonException;
import com.nep.common.exception.CommonErrorCode;
import com.nep.system.entity.Role;
import com.nep.common.exception.UserErrorCode;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.nep.system.entity.RoleUser;
import java.util.List;
import com.nep.system.dto.LoginRequest;
import com.nep.system.vo.CurrentUserVO;
import com.nep.system.vo.LoginResponse;
import org.springframework.beans.BeanUtils;
import com.nep.security.jwt.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_ROLE_CODE = "ROLE_USER";

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final RoleUserMapper roleUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 用户注册
     * 
     * @param request 用户注册请求参数，包含用户名、邮箱和密码
     * @return 注册成功后的用户信息视图对象
     */
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
                        .last("limit 1"));

        if (defaultRole == null) {
            throw new CommonException(CommonErrorCode.SYSTEM_ERROR,
                    "默认角色 ROLE_USER 不存在，请检查数据库初始化脚本");
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
                user.getEmail());
    }


    /**
     * 用户登录
     * 
     * @param request 用户登录请求参数，包含用户名或邮箱和密码
     * @return 登录成功后的用户信息视图对象
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        String account = request.getAccount().trim(); // 从请求参数中提取用户名或邮箱
        String emailAccount = account.toLowerCase(Locale.ROOT); // 转换为小写邮箱

        // 查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .and(wrapper -> wrapper
                                .eq(User::getUsername, account)
                                .or()
                                .eq(User::getEmail, emailAccount))
                        .last("limit 1"));

        // 校验用户是否存在且密码匹配
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new CommonException(UserErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 校验用户是否已删除或禁用
        if (!Objects.equals(user.getIsDeleted(), FieldConstant.NOT_DELETED)
                || !Objects.equals(user.getStatus(), FieldConstant.ENABLED)) {
            throw new CommonException(UserErrorCode.USER_DISABLED);
        }

        // 获取用户的所有角色编码
        List<String> roleCodes = getUserRoleCodes(user.getId());

        LocalDateTime now = LocalDateTime.now();
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setLastLoginTime(now);
        updateUser.setUpdateTime(now);
        // 更新用户信息
        userMapper.updateById(updateUser);

        // 生成访问令牌
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), roleCodes);

        // 构建登录响应VO
        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType(AuthConstant.TOKEN_TYPE_BEARER)
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .user(CurrentUserVO.builder()
                        .id(String.valueOf(user.getId()))
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .avatar(user.getAvatar())
                        .roles(roleCodes)
                        .build())
                .build();
    }

    /**
     * 检查用户名是否存在
     * 如果用户名存在，则抛出异常
     * 
     * @param username 用户名
     * @throws CommonException 如果用户名存在
     */
    private void checkUsernameNotExists(String username) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
                        .eq(User::getIsDeleted, FieldConstant.NOT_DELETED)
                        .eq(User::getStatus, FieldConstant.ENABLED));

        if (count != null && count > 0) {
            throw new CommonException(UserErrorCode.USERNAME_EXISTS);
        }
    }

    /**
     * 检查邮箱是否存在
     * 如果邮箱存在，则抛出异常
     * 
     * @param email 邮箱
     * @throws CommonException 如果邮箱存在
     */
    private void checkEmailNotExists(String email) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getEmail, email)
                        .eq(User::getIsDeleted, FieldConstant.NOT_DELETED)
                        .eq(User::getStatus, FieldConstant.ENABLED));

        if (count != null && count > 0) {
            throw new CommonException(UserErrorCode.EMAIL_EXISTS);
        }
    }

    /**
     * 获取用户的所有角色编码
     * 如果用户没有角色，则返回空列表
     * 
     * @param userId 用户ID
     * @return 用户的所有角色编码列表
     */
    private List<String> getUserRoleCodes(Long userId) {
        // 查询用户的所有角色
        List<RoleUser> roleUsers = roleUserMapper.selectList(
                new LambdaQueryWrapper<RoleUser>()
                        .eq(RoleUser::getUserId, userId));

        // 如果用户没有角色，返回空列表
        if (roleUsers == null || roleUsers.isEmpty()) {
            return List.of();
        }

        // 提取角色ID列表
        List<Integer> roleIds = roleUsers.stream()
                .map(RoleUser::getRoleId) // 提取角色ID, 转换为Integer类型
                .distinct()
                .toList();

        return roleMapper.selectByIds(roleIds)
                .stream()
                .filter(role -> Objects.equals(role.getIsDeleted(), FieldConstant.NOT_DELETED))
                .map(Role::getRoleCode)
                .distinct()
                .toList();
    }
}
