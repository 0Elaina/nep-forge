package com.nep.controller;

import java.util.Map;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nep.common.result.ApiResponse;
import com.nep.common.result.PageResult;
import com.nep.hardware.dto.HardwareQueryRequest;
import com.nep.hardware.dto.HardwareSaveRequest;
import com.nep.hardware.vo.HardwareListVO;
import com.nep.hardware.service.HardwareService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "后台配件管理接口")
@RestController
@RequestMapping("/api/v1/admin/hardware")
@RequiredArgsConstructor
@Slf4j
public class AdminHardwareController {
    private final HardwareService hardwareService;

     /**
      * 查询配件列表
      * @param request 查询参数
      * @return 配件列表
      */
    @Operation(summary = "查询配件列表")
    @GetMapping
    public ApiResponse<PageResult<HardwareListVO>> listHardware(@Valid @ParameterObject HardwareQueryRequest request) {
        log.info("后台管理查询配件列表，请求参数：{}", request);
        return ApiResponse.success(hardwareService.listHardware(request));
    }

    /**
     * 创建配件
     * @param request 创建参数
     * @return 创建的配件ID
     */
    @Operation(summary = "后台创建配件")
    @PostMapping
    public ApiResponse<Map<String, String>> createHardware(
        @Valid @RequestBody HardwareSaveRequest request
    ) {
        Long id = hardwareService.createHardware(request);
        log.info("后台创建配件, 配件ID: {}", id);
        return ApiResponse.success(Map.of("id", String.valueOf(id)));
    }

    /**
     * 更新配件
     * @param id 配件ID
     * @param request 更新参数
     * @return 是否更新成功
     */
    @Operation(summary = "后台更新配件")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> updateHardware(
        @PathVariable Long id,
        @Valid @RequestBody HardwareSaveRequest request
    ) {
        log.info("后台更新配件, 配件ID: {}, 更新参数: {}", id, request);
        return ApiResponse.success(hardwareService.updateHardware(id, request));
    }

    /**
     * 删除配件
     * @param id 配件ID
     * @return 是否删除成功
     */
    @Operation(summary = "后台删除配件")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteHardware(@PathVariable Long id) {
        log.info("后台删除配件, 配件ID: {}", id);
        return ApiResponse.success(hardwareService.deleteHardware(id));
    }
}
