package com.nep.hardware.service;

import com.nep.common.result.PageResult;
import com.nep.hardware.dto.HardwareQueryRequest;
import com.nep.hardware.vo.HardwareDetailVO;
import com.nep.hardware.vo.HardwareListVO;

/**
 * 配件服务接口
 * 提供配件相关的业务逻辑，如查询、插入、更新和删除。
 */
public interface HardwareService {
    /**
     * 查询配件列表
     * @param request 查询参数
     * @return 配件列表
     */
    PageResult<HardwareListVO> listHardware(HardwareQueryRequest request);    

    /**
     * 查询配件详情
     * @param id 配件ID
     * @return 配件详情VO
     */
    HardwareDetailVO getHardwareDetail(Long id);
}
