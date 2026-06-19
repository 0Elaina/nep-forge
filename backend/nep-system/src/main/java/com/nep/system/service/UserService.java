package com.nep.system.service;

import com.nep.system.vo.CurrentUserDetailVO;

public interface UserService {
    
    /**
     * 获取当前用户详情
     * 
     * @param userId 用户ID
     * @return 当前用户详情VO
     */
    CurrentUserDetailVO getCurrentUser(Long userId);
}
