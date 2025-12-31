package cn.net.yunlou.bole.model.create;

import cn.net.yunlou.bole.common.BaseCreate;
import cn.net.yunlou.bole.model.entity.Contact;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackCreate extends BaseCreate {

    private String type;

    private String content;

    private List<String> images;

    private Contact contact;
}
