package cn.net.yunlou.bole.constant;

import cn.net.yunlou.bole.common.KeyFieldEnum;
import cn.net.yunlou.bole.common.SkipInvalidValueLambdaQueryWrapper;
import cn.net.yunlou.bole.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FileName: CompanyKeyFieldEnum
 * Description:
 * Created By laughtiger
 * Created At 2025/11/23 22:57
 * Modified By
 * Modified At
 */
@SuppressWarnings("unchecked")
@Getter
@AllArgsConstructor
public enum CompanyKeyFieldEnum implements KeyFieldEnum<String> {

    HOLDER("holder","按企业股东查找"){
        @Override
        public <T> void applyQuery(SkipInvalidValueLambdaQueryWrapper<T> wrapper, String keywords) {
            SkipInvalidValueLambdaQueryWrapper<Company> userWrapper =
                    (SkipInvalidValueLambdaQueryWrapper<Company>) wrapper;
            userWrapper.likeRight(Company::getHolder, keywords);
        }
    }
    ,EMAIL("email", "按企业邮箱查找") {
        @Override
        public <T> void applyQuery(SkipInvalidValueLambdaQueryWrapper<T> wrapper, String keywords) {
            SkipInvalidValueLambdaQueryWrapper<Company> userWrapper =
                    (SkipInvalidValueLambdaQueryWrapper<Company>) wrapper;
            userWrapper.likeRight(Company::getEmail, keywords);
        }
    }, NAME("name", "按企业名称查找") {
        @Override
        public <T> void applyQuery(SkipInvalidValueLambdaQueryWrapper<T> wrapper, String keywords) {
            SkipInvalidValueLambdaQueryWrapper<Company> userWrapper =
                    (SkipInvalidValueLambdaQueryWrapper<Company>) wrapper;
            userWrapper.likeRight(Company::getName, keywords);
        }
    }, ALL("all", "按企业名称、企业邮箱、企业股东查找") {
        @Override
        public <T> void applyQuery(SkipInvalidValueLambdaQueryWrapper<T> wrapper, String keywords) {
            SkipInvalidValueLambdaQueryWrapper<Company> userWrapper = (SkipInvalidValueLambdaQueryWrapper<Company>) wrapper;
            userWrapper.and(w ->
                    w.likeRight(Company::getName, keywords)
                            .or().likeRight(Company::getHolder, keywords)
                            .or().likeRight(Company::getEmail, keywords));
        }
    };

    public final String value;

    public final String label;
}
