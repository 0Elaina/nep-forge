package com.nep.system.service.impl;

import java.util.Objects;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nep.common.constants.FieldConstant;
import com.nep.common.constants.MessageConstant;
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
import com.nep.system.dto.UserProfileUpdateRequest;
import com.nep.system.entity.Role;
import com.nep.system.entity.RoleUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

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
     * 更新当前用户个人信息
     * 
     * @param userId 用户ID
     * @param request 更新个人信息请求DTO
     * @return 更新后的当前用户详情VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CurrentUserDetailVO updateCurrentUserProfile(Long userId, UserProfileUpdateRequest request) {
        // 校验用户ID是否存在
        if (userId == null) {
            throw new CommonException(CommonErrorCode.UNAUTHORIZED);
        }

        // 校验请求参数是否存在
        if (request == null) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR);
        }

        // 根据用户ID查询用户信息
        User user = userMapper.selectById(userId);

        // 校验用户是否存在
        if (user == null) {
            throw new CommonException(CommonErrorCode.UNAUTHORIZED);
        }

        // 校验用户是否已删除或禁用
        if (!Objects.equals(user.getIsDeleted(), FieldConstant.NOT_DELETED)
                || !Objects.equals(user.getStatus(), FieldConstant.ENABLED)) {
            throw new CommonException(UserErrorCode.USER_DISABLED);
        }

        // 构建更新条件
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .eq(User::getIsDeleted, FieldConstant.NOT_DELETED)
                .eq(User::getStatus, FieldConstant.ENABLED);

        boolean hasUpdateField = false;

        // 如果请求参数包含头像, 则更新头像字段
        if (request.getAvatar() != null) {
            updateWrapper.set(User::getAvatar, normalizeNullableText(request.getAvatar()));
            hasUpdateField = true;
        }

        // 如果请求参数包含个人简介, 则更新个人简介字段
        if (request.getBio() != null) {
            updateWrapper.set(User::getBio, normalizeNullableText(request.getBio()));
            hasUpdateField = true;
        }

        // 如果请求参数包含昵称, 则更新昵称字段
        if (request.getNickname() != null) {
            updateWrapper.set(User::getNickname, normalizeNullableText(request.getNickname()));
            hasUpdateField = true;
        }

        // 如果请求参数中没有包含可更新字段, 则抛出异常
        if (!hasUpdateField) {
            throw new CommonException(
                CommonErrorCode.REQUEST_PARAM_ERROR,
                MessageConstant.USER_PROFILE_UPDATE_EMPTY
            );
        }

        /**
         * 手动设置更新时间。
         *
         * NepMetaObjectHandler 中定义的 strictUpdateFill 只对实体对象
         * （如 updateById(entity)、update(entity, wrapper)）生效，
         * 而本方法使用 LambdaUpdateWrapper.set() 做部分字段更新，
         * 不走实体对象，因此自动填充不会触发，需要手动设置。
         */
        updateWrapper.set(User::getUpdateTime, LocalDateTime.now());

        // 执行更新操作
        int updatedRows = userMapper.update(null, updateWrapper);
        // 校验更新结果, 如果更新行数不是1, 则抛出异常
        if (updatedRows != 1) {
            throw new CommonException(
                CommonErrorCode.SYSTEM_ERROR,
                MessageConstant.USER_PROFILE_UPDATE_FAILED
            );
        }

        return getCurrentUser(userId);
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


    /**
     * 规范可空文本值
     * 
     * @param value 可空文本值
     * @return 规范后的文本值
     */
    private String normalizeNullableText(String value) {
        if (value == null) return null;

        // 移除首尾空格, 如果为空字符串则返回null
        return value.trim().isEmpty() ? null : value.trim();
    }

}
