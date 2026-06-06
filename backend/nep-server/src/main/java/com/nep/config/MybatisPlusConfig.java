package com.nep.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 插件配置。
 * <p>
 * 注册 MyBatis-Plus 框架所需的拦截器插件，当前配置了分页拦截器，
 * 支持对 MySQL 数据库进行物理分页查询。
 * </p>
 *
 * @author Neptune
 * @since 1.0
 */
@Configuration
@MapperScan("com.nep.**.mapper")
public class MybatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 拦截器，添加分页插件。
     * 分页拦截器启用后，Mapper 方法中声明 {@link com.baomidou.mybatisplus.extension.plugins.pagination.Page}
     * 参数即可自动拼接分页 SQL，无需手动编写分页逻辑。
     *
     * @return MybatisPlusInterceptor 实例，已注册 MySQL 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
