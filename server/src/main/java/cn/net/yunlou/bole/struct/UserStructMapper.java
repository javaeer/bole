package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.model.dto.UserDTO;
import cn.net.yunlou.bole.model.query.UserQuery;
import org.mapstruct.Mapper;

/**
 * FileName: UserStructMapper
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/26 19:17
 * Modified By
 * Modified At
 */
@Mapper(componentModel = "spring")
public interface UserStructMapper extends BaseStructMapper<User, UserDTO, UserQuery> {
}
