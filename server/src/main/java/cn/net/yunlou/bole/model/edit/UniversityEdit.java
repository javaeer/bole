package cn.net.yunlou.bole.model.edit;

import cn.net.yunlou.bole.common.BaseEdit;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: UniversityCreate
 * Description:
 * Created By laughtiger
 * Created At 2025/12/30 17:38
 * Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UniversityEdit extends BaseEdit {

    private String name;

    private String code;

    /**
     * 985 211 重点 普通
     */
    private String type;

    private String holder;

    private String location;

    private String website;

    private String github;

    private String bio;

    private Integer followers;

    private Integer fans;

    private Integer likes;
}
