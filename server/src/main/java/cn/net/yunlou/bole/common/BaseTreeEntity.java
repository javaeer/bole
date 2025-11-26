package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * FileName: BaseTreeEntity
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/25 17:00
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseTreeEntity<T> extends BaseEntity {

    private Long parentId = 0L;

    private String path;

    private Integer level = 1;

    @TableField(exist = false)
    private List<T> children;

    @TableField(exist = false)
    private T parent;
}
