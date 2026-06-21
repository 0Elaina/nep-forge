package com.nep.hardware.service;

import com.nep.common.result.PageResult;
import com.nep.hardware.dto.HardwareCompareRequest;
import com.nep.hardware.dto.HardwareQueryRequest;
import com.nep.hardware.dto.HardwareSaveRequest;
import com.nep.hardware.vo.HardwareCompareVO;
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


    /**
     * 对比配件
     * @param request 对比参数
     * @return 对比结果
     */
    HardwareCompareVO compareHardware(HardwareCompareRequest request);

    /**
     * 创建配件
     * @param request
     * @return 创建的配件ID
     */
    Long createHardware(HardwareSaveRequest request);

    /**
     * 更新配件
     * @param id 配件ID
     * @param request
     * @return 是否更新成功
     */
    Boolean updateHardware(Long id, HardwareSaveRequest request);

    /**
     * 删除配件
     * @param id 配件ID
     * @return 是否删除成功
     */
    Boolean deleteHardware(Long id);


}
