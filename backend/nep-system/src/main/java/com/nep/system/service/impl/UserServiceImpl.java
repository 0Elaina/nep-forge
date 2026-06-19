package com.nep.system.service.impl;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.nep.common.constants.FieldConstant;
import com.nep.common.exception.CommonErrorCode;
import com.nep.system.mapper.RoleMapper;
import com.nep.system.mapper.RoleUserMapper;
import com.nep.system.mapper.UserMapper;
import com.nep.system.service.UserService;
import com.nep.system.vo.CurrentUserDetailVO;

import lombok.RequiredArgsConstructor;
import com.nep.common.exception.CommonException;
import com.nep.common.exception.UserErrorCode;
import com.nep.system.entity.User;
import com.nep.system.entity.Role;
import com.nep.system.entity.RoleUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final RoleUserMapper roleUserMapper;

    /**
     * 获取当前用户详情VO
     * 
     * @param userId 用户ID
     * @return 当前用户详情VO
     */
    @Override
    public CurrentUserDetailVO getCurrentUser(Long userId) {
        if (userId == null) {
            throw new CommonException(CommonErrorCode.UNAUTHORIZED);
        }

        User user = userMapper.selectById(userId);

        if (user == null) {
            throw new CommonException(CommonErrorCode.UNAUTHORIZED);
        }

        if (!Objects.equals(user.getIsDeleted(), FieldConstant.NOT_DELETED)
                || !Objects.equals(user.getStatus(), FieldConstant.ENABLED)) {
            throw new CommonException(UserErrorCode.USER_DISABLED);
        }

        // 获取用户角色编码列表
        List<String> roleCodes = getUserRoleCodes(userId);

        return CurrentUserDetailVO.builder()
                .id(String.valueOf(user.getId()))
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .bio(user.getBio())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .lastLoginTime(
                        user.getLastLoginTime() == null ? null : user.getLastLoginTime().format(DATE_TIME_FORMATTER))
                .roles(roleCodes)
                .createTime(user.getCreateTime() == null ? null : user.getCreateTime().format(DATE_TIME_FORMATTER))
                .build();

    }

    /**
     * 获取用户角色编码列表
     * 
     * @param userId 用户ID
     * @return 用户角色编码列表
     */
    private List<String> getUserRoleCodes(Long userId) {
        List<RoleUser> roleUsers = roleUserMapper.selectList(
                new LambdaQueryWrapper<RoleUser>()
                        .eq(RoleUser::getUserId, userId));

        if (roleUsers == null || roleUsers.isEmpty()) {
            return List.of();
        }

        List<Integer> roleIds = roleUsers.stream()
                .map(RoleUser::getRoleId)
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
