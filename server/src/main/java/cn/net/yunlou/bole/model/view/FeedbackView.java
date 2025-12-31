package cn.net.yunlou.bole.model.view;

import cn.net.yunlou.bole.common.BaseView;
import cn.net.yunlou.bole.model.entity.Contact;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackView extends BaseView {

    private String type;

    private String content;

    private List<String> images;

    private Contact contact;
}
