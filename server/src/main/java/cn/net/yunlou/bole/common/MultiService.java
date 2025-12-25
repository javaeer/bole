package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * FileName: MultiService Description: Created By MR. WANG Created At 2025/11/19 15:32 Modified By
 * Modified At
 */
@Slf4j
@Transactional(readOnly = true)
public abstract class MultiService<
        M extends IMultiMapper<T, L, R>,
        T extends MultiEntity,
        L extends BaseEntity,
        R extends BaseEntity>
        extends ServiceImpl<M, T> implements IMultiService<T, L, R> {

    @Override
    public boolean exists(T entity) {
        return super.exists(SkipInvalidValueWrappers.query(entity));
    }

    @Override
    public List<L> listLeft(T entity) {
        return baseMapper.selectListLeft(entity);
    }

    @Override
    public Set<Long> listLeftIds(T entity) {

        List<L> ls = listLeft(entity);

        if (ls == null) {
            return Collections.emptySet();
        }

        return ls.stream()
                .map(BaseEntity::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public List<R> listRight(T entity) {
        return baseMapper.selectListRight(entity);
    }

    @Override
    public Set<Long> listRightIds(T entity) {

        List<R> rs = listRight(entity);

        if (rs == null) {
            return Collections.emptySet();
        }

        return rs.stream()
                .map(BaseEntity::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public Page<L> pageLeft(long pageNum, long pageSize, T entity) {
        Page<L> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        return baseMapper.selectListLeft(page, entity);
    }

    @Override
    public Page<R> pageRight(long pageNum, long pageSize, T entity) {
        Page<R> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        return baseMapper.selectListRight(page, entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bind(L left, R right) {
        return save(createEntity(left, right));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbind(L left, R right) {
        return remove(Wrappers.lambdaQuery(createEntity(left, right)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sync(L left, List<R> rights) {
        // 1. 删除当前 left 的所有关联
        T deleteCondition = createEntity(left, null);
        remove(Wrappers.lambdaQuery(deleteCondition));

        // 2. 批量插入新关联
        if (rights != null && !rights.isEmpty()) {
            List<T> joins =
                    rights.stream()
                            .map(right -> createEntity(left, right))
                            .collect(Collectors.toList());
            return saveBatch(joins);
        }
        return true;
    }

    protected abstract T createEntity(L left, R right);
}
