package com.nep.system.service;

import com.nep.system.dto.UserProfileUpdateRequest;
import com.nep.system.vo.CurrentUserDetailVO;

public interface UserService {
    
    /**
     * 获取当前用户详情
     * 
     * @param userId 用户ID
     * @return 当前用户详情VO
     */
    CurrentUserDetailVO getCurrentUser(Long userId);

    /**
     * 更新当前用户个人信息
     * 
     * @param userId 用户ID
     * @param request 更新个人信息请求DTO
     * @return 更新后的当前用户详情VO
     */
    CurrentUserDetailVO updateCurrentUserProfile(Long userId, UserProfileUpdateRequest request);
}
