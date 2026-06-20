package com.nep.common.handler;

import java.time.LocalDateTime;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

/**
 * 自定义元对象处理类
 * 用于在插入和更新操作中自动填充创建时间和更新时间。
 */
@Component
public class NepMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充创建时间和更新时间
     * @param metaObject 用于操作数据库记录的元对象
     * @return void
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();

        /**
         * 参数解析
         *      metaObject: 用于操作数据库记录的元对象
         *      field: 字段名
         *      fieldType: 字段类型
         *      value: 值
         */
        strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
    }

    /**
     * 更新时自动填充更新时间
     * @param metaObject 用于操作数据库记录的元对象
     * @return void
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
