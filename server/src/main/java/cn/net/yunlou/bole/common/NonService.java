package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * FileName: NonService Description: Created By laughtiger Created At 2025/12/13 00:14 Modified By
 * Modified At
 */
@Slf4j
@Transactional(readOnly = true)
public abstract class NonService<M extends BaseMapper<T>, T extends NonEntity>
        extends ServiceImpl<M, T> implements INonService<T> {}
