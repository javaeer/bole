package cn.net.yunlou.bole.model.query;

import cn.net.yunlou.bole.common.BaseQuery;
import cn.net.yunlou.bole.entity.Contact;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackQuery extends BaseQuery {

    private String type;

    private String content;

    private List<String> images;

    private Contact contact;
}
