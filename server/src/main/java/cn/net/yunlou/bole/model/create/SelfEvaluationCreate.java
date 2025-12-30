package cn.net.yunlou.bole.model.create;

import cn.net.yunlou.bole.common.BaseCreate;
import cn.net.yunlou.bole.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * FileName: SelfEvaluationCreate Description: Created By laughtiger Created At 2025/12/14 04:09
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data

public class SelfEvaluationCreate extends BaseCreate {

    private String content;

    //亮点
    private List<String> highlights;
}
