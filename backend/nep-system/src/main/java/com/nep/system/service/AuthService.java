package com.nep.system.service;

import com.nep.system.dto.RegisterRequest;
import com.nep.system.vo.RegisterVO;
import com.nep.system.vo.LoginResponse;
import com.nep.system.dto.LoginRequest;

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

    /**
     * 用户登录。
     * 接收用户提交的登录信息，校验通过后生成访问令牌并返回登录结果。
     *
     * @param request 用户登录请求参数，包含用户名或邮箱和密码
     * @return 登录成功后的登录响应视图对象，包含访问令牌、令牌类型、过期时间、当前用户信息等
     */
    LoginResponse login(LoginRequest request);

    /**
     * 退出登录。
     * 清除当前用户的访问令牌，使用户无法继续使用系统服务。
     *
     * @return true 如果退出成功，否则返回 false
     * @return false 如果退出失败，例如用户未登录或会话已过期
     */
    Boolean logout();
}
