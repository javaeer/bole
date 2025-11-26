package cn.net.yunlou.bole.common.constant;

import cn.net.yunlou.bole.common.KeyFieldEnum;
import cn.net.yunlou.bole.common.SkipInvalidValueLambdaQueryWrapper;
import cn.net.yunlou.bole.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FileName: UserKeyFieldEnum
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/23 22:57
 * Modified By
 * Modified At
 */
@SuppressWarnings("unchecked")
@Getter
@AllArgsConstructor
public enum UserKeyFieldEnum implements KeyFieldEnum<String> {
    EMAIL("email", "按邮箱查找") {
        @Override
        public <T> void applyQuery(SkipInvalidValueLambdaQueryWrapper<T> wrapper, String keywords) {
            SkipInvalidValueLambdaQueryWrapper<User> userWrapper =
                    (SkipInvalidValueLambdaQueryWrapper<User>) wrapper;
            userWrapper.likeRight(User::getEmail, keywords);
        }
    }, PHONE("phone", "按手机查找") {
        @Override
        public <T> void applyQuery(SkipInvalidValueLambdaQueryWrapper<T> wrapper, String keywords) {
            SkipInvalidValueLambdaQueryWrapper<User> userWrapper =
                    (SkipInvalidValueLambdaQueryWrapper<User>) wrapper;
            userWrapper.likeRight(User::getPhone, keywords);
        }
    }, NAME("name", "按名称查找") {
        @Override
        public <T> void applyQuery(SkipInvalidValueLambdaQueryWrapper<T> wrapper, String keywords) {
            SkipInvalidValueLambdaQueryWrapper<User> userWrapper =
                    (SkipInvalidValueLambdaQueryWrapper<User>) wrapper;
            userWrapper.likeRight(User::getName, keywords);
        }
    }, ALL("all", "按名称、电话、邮箱查找") {
        @Override
        public <T> void applyQuery(SkipInvalidValueLambdaQueryWrapper<T> wrapper, String keywords) {
            SkipInvalidValueLambdaQueryWrapper<User> userWrapper = (SkipInvalidValueLambdaQueryWrapper<User>) wrapper;
            userWrapper.and(w ->
                    w.likeRight(User::getName, keywords)
                            .or().likeRight(User::getPhone, keywords)
                            .or().likeRight(User::getEmail, keywords));
        }
    };

    public final String value;

    public final String label;
}
