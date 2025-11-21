package cn.net.yunlou.bole.common;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * FileName: ManyEntity
 * Description: 多主键 实体
 * Created By laughtiger
 * Created At 2025/11/19 15:21
 * Modified By
 * Modified At
 */
@Data
public class MultiEntity<L extends BaseEntity, R extends BaseEntity> implements Serializable {}
