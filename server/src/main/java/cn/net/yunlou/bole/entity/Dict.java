package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseTreeEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: Dict
 * Description:
 * Created By laughtiger
 * Created At 2025/11/30 21:33
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_dict")
public class Dict extends BaseTreeEntity<Dict> {

    private String name;

    private String type;

    private String code;

    private String value;

    private String label;

    private Integer state;
}
