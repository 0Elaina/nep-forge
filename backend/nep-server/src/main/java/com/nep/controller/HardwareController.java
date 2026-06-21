package com.nep.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.nep.common.result.ApiResponse;
import com.nep.common.result.PageResult;
import com.nep.hardware.dto.HardwareCompareRequest;
import com.nep.hardware.dto.HardwareQueryRequest;
import com.nep.hardware.service.HardwareService;
import com.nep.hardware.vo.HardwareCompareVO;
import com.nep.hardware.vo.HardwareDetailVO;
import com.nep.hardware.vo.HardwareListVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 配件接口
 * 提供配件相关的API接口，如查询、插入、更新和删除。
 */
@Tag(name = "配件接口")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/hardware")
public class HardwareController {
    private final HardwareService hardwareService;

    /**
     * 获取配件分页列表
     * @param request 查询参数
     * @return 配件列表 分页结果
     */
    @Operation(summary = "获取配件分页列表")
    @GetMapping("/list")
    public ApiResponse<PageResult<HardwareListVO>> listHardware(
        @Valid @ParameterObject HardwareQueryRequest request
    ) {
        log.info("获取配件分类列表，请求参数：{}", request);
        return ApiResponse.success(hardwareService.listHardware(request));
    }

    /**
     * 获取配件详情
     * @param id 配件ID
     * @return 配件详情
     */
    @Operation(summary = "获取配件详情")
    @GetMapping("/{id}")
    public ApiResponse<HardwareDetailVO> getHardwareDetail(@PathVariable Long id) {
        log.info("获取配件详情, 配件ID: {}", id);
        return ApiResponse.success(hardwareService.getHardwareDetail(id));
    }

    /**
     * 多配件参数对比
     * @param request 对比参数
     * @return 对比结果
     */
    @Operation(summary = "多配件参数对比")
    @PostMapping("/compare")
    public ApiResponse<HardwareCompareVO> compareHardware(
        @Valid @RequestBody HardwareCompareRequest request
    ) {
        log.info("多配件参数对比, 对比参数: {}", request);
        return ApiResponse.success(hardwareService.compareHardware(request));
    }
}
