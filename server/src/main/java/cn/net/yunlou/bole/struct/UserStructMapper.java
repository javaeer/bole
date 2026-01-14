package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.model.create.UserCreate;
import cn.net.yunlou.bole.model.edit.UserEdit;
import cn.net.yunlou.bole.model.entity.User;
import cn.net.yunlou.bole.model.query.UserQuery;
import cn.net.yunlou.bole.model.view.UserBasicInfoView;
import cn.net.yunlou.bole.model.view.UserView;
import java.util.Map;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: UserStructMapper Description: Created By MR. WANG Created At 2025/11/26 19:17 Modified
 * By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface UserStructMapper
        extends BaseStructMapper<User, UserCreate, UserView, UserEdit, UserQuery> {

    static Map<String, Object> toPreviewProps(Long userId) {
        return null;
    }

    UserBasicInfoView toBasicInfoView(User user);
}
