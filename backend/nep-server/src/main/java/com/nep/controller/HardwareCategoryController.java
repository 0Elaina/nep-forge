package com.nep.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nep.common.result.ApiResponse;
import com.nep.hardware.service.HardwareCategoryService;
import com.nep.hardware.vo.HardwareCategoryTreeVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 配件分类控制器
 * 提供配件分类相关的API接口，用于获取配件分类树等操作。
 */
@Tag(name="配件分类接口")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hardware/categories")
public class HardwareCategoryController {
    private final HardwareCategoryService hardwareCategoryService;

    /**
     * 获取配件分类树
     * @return 配件分类树列表
     */
    @Operation(summary="获取配件分类树")
    @GetMapping("/tree")
    public ApiResponse<List<HardwareCategoryTreeVO>> listCategoryTree() {
        log.info("获取配件分类树");
        return ApiResponse.success(hardwareCategoryService.listCategoryTree());
    }
}
