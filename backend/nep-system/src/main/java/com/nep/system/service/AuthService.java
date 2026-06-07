package com.nep.system.service;

import com.nep.system.dto.RegisterRequest;
import com.nep.system.vo.RegisterVO;

/**
 * 认证服务接口。
 * 负责处理用户注册、登录、鉴权等认证相关业务逻辑。
 *
 * @author nep
 */
public interface AuthService {
    
    /**
     * 用户注册。
     * 接收用户提交的注册信息，校验通过后创建新用户并返回注册结果。
     *
     * @param request 用户注册请求参数，包含用户名、邮箱和密码
     * @return 注册成功后的用户信息视图对象
     */
    RegisterVO register(RegisterRequest request);
}
