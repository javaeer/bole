package cn.net.yunlou.bole.model.edit;

import cn.net.yunlou.bole.common.BaseEdit;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * FileName: SelfEvaluationEdit Description: Created By laughtiger Created At 2025/12/14 04:10
 * Modified By Modified At
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class SelfEvaluationEdit extends BaseEdit {

    private String content;

    //亮点
    private List<String> highlights;
}
