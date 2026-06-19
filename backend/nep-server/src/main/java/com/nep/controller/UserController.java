package com.nep.controller;

import org.springframework.web.bind.annotation.RestController;

import com.nep.common.constants.AuthConstant;
import com.nep.common.exception.CommonErrorCode;
import com.nep.common.exception.CommonException;
import com.nep.common.result.ApiResponse;
import com.nep.security.jwt.JwtTokenProvider;
import com.nep.security.jwt.JwtUserInfo;
import com.nep.system.service.UserService;
import com.nep.system.vo.CurrentUserDetailVO;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "用户接口")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 获取当前登录用户信息。
     *     从Authorization头中提取Bearer令牌，解析令牌中的用户信息，
     *     并根据用户ID从数据库中查询用户详情。
     *        登录用户信息。
     * 
     * @param authorizationHeader Authorization头内容，格式为 "Bearer <token>"
     * @return 当前登录用户的详情VO图对象
     * 
     * @throws CommonException
     *                         如果Authorization头为空或不以Bearer开头，抛出CommonException异常。
     */
    @Operation(summary = "获取当前登录用户")
    @GetMapping("/me")
    public ApiResponse<CurrentUserDetailVO> getCurrentUser(
            @RequestHeader(value = "AUTHORIZATION_HEADER", required = false) String authorizationHeader) {
        log.info("当前登录用户: {}", authorizationHeader);

        // 从Authorization头中提取Bearer令牌
        String token = extractBearerToken(authorizationHeader);

        JwtUserInfo jwtUserInfo;
        try {
            jwtUserInfo = jwtTokenProvider.parseAccessToken(token);
        } catch (IllegalArgumentException e) {
            throw new CommonException(CommonErrorCode.UNAUTHORIZED);
        }

        return ApiResponse.success(userService.getCurrentUser(jwtUserInfo.userId()));
    }

    /**
     * 从Authorization头中提取Bearer令牌。
     * 
     * @param authorization Authorization头内容，格式为 "Bearer <token>"
     * @return 提取的Bearer令牌字符串
     * 
     * @throws CommonException
     *                         如果Authorization头为空或不以Bearer开头，抛出CommonException异常。
     */
    private String extractBearerToken(String authorization) {
        // 从Authorization头中提取Bearer令牌
        // Authorization头格式为 "Bearer <token>"
        String prefix = AuthConstant.TOKEN_TYPE_BEARER + " ";

        // 检查Authorization头是否存在且以Bearer开头
        // hasText() 方法用于检查字符串是否为空或仅包含空格
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(prefix)) {
            throw new CommonException(CommonErrorCode.UNAUTHORIZED);
        }

        return authorization.substring(prefix.length());
    }
}
