package cn.net.yunlou.bole.model.edit;

import cn.net.yunlou.bole.common.BaseEdit;
import cn.net.yunlou.bole.model.entity.Contact;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackEdit extends BaseEdit {

    private String type;

    private String content;

    private List<String> images;

    private Contact contact;
}
