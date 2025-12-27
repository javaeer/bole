package cn.net.yunlou.bole.common.constant;

import cn.net.yunlou.bole.common.IEnum;
import cn.net.yunlou.bole.common.IEnumCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户状态枚举
 */
@Getter
@AllArgsConstructor
public enum UserStatus implements IEnumCode<Integer, String> {
    /**
     * 待激活（新注册用户）
     */
    PENDING(0, "待激活", "PENDING"),

    /**
     * 活跃/正常状态
     */
    ACTIVE(1, "活跃", "ACTIVE"),

    /**
     * 已禁用（管理员操作）
     */
    DISABLED(2, "已禁用", "DISABLED"),

    /**
     * 已锁定（登录失败过多）
     */
    LOCKED(3, "已锁定", "LOCKED"),

    /**
     * 已注销（用户主动注销）
     */
    CANCELLED(4, "已注销", "CANCELLED"),

    /**
     * 已过期（账户到期）
     */
    EXPIRED(5, "已过期", "EXPIRED"),

    /**
     * 待审核（需要管理员审核）
     */
    PENDING_REVIEW(6, "待审核", "PENDING_REVIEW"),

    /**
     * 审核不通过
     */
    REVIEW_REJECTED(7, "审核不通过", "REVIEW_REJECTED");

    private final Integer value;
    private final String label;
    private final String code;

    // 方便调用的快捷方法
    public static UserStatus codeOf(String code) {
        return IEnumCode.codeOf(code, UserStatus.class);
    }

    public static UserStatus codeOfSafe(String code) {
        return IEnumCode.codeOfSafe(code, UserStatus.class);
    }

    public static String codeOfLabel(String code) {
        return IEnumCode.codeOfLabel(code, UserStatus.class);
    }

    public static Integer codeOfValue(String code) {
        return IEnumCode.codeOfValue(code, UserStatus.class);
    }

    public static boolean isCode(String code) {
        return IEnumCode.isCode(code, UserStatus.class);
    }

    public static List<String> codes() {
        return IEnumCode.codes(UserStatus.class);
    }

    public static List<IEnumCode.EnumCodeItem<Integer, String>> toEnumCodeItemList() {
        return IEnumCode.toEnumCodeItemList(UserStatus.class);
    }

    public static Map<String, IEnum.EnumItem<Integer>> toEnumCodeItemMap() {
        return IEnumCode.toEnumCodeItemMap(UserStatus.class);
    }

    // 业务逻辑方法
    public static boolean isActive(Integer statusValue) {
        return ACTIVE.getValue().equals(statusValue);
    }

    public static boolean isLocked(Integer statusValue) {
        return LOCKED.getValue().equals(statusValue);
    }

    public static boolean isDisabled(Integer statusValue) {
        return DISABLED.getValue().equals(statusValue);
    }

    public static List<UserStatus> getLoginableStatuses() {
        return Arrays.asList(ACTIVE, PENDING);
    }

    public static List<UserStatus> getManageableStatuses() {
        return Arrays.asList(ACTIVE, DISABLED, LOCKED, PENDING_REVIEW);
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isLocked() {
        return this == LOCKED;
    }

    public boolean isDisabled() {
        return this == DISABLED;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isValidForLogin() {
        return this == ACTIVE || this == PENDING;
    }

    public boolean needsActivation() {
        return this == PENDING;
    }

    public boolean needsReview() {
        return this == PENDING_REVIEW;
    }

    public boolean isTerminal() {
        return this == CANCELLED || this == REVIEW_REJECTED;
    }

    public boolean canTransitionTo(UserStatus targetStatus) {
        if (targetStatus == null) {
            return false;
        }

        if (this == targetStatus) {
            return true;
        }

        if (isTerminal()) {
            return false;
        }

        return switch (this) {
            case PENDING -> targetStatus == ACTIVE ||
                    targetStatus == DISABLED ||
                    targetStatus == LOCKED ||
                    targetStatus == PENDING_REVIEW;
            case ACTIVE -> targetStatus == DISABLED ||
                    targetStatus == LOCKED ||
                    targetStatus == CANCELLED ||
                    targetStatus == EXPIRED ||
                    targetStatus == PENDING_REVIEW;
            case DISABLED -> targetStatus == ACTIVE ||
                    targetStatus == LOCKED ||
                    targetStatus == CANCELLED;
            case LOCKED -> targetStatus == ACTIVE ||
                    targetStatus == DISABLED ||
                    targetStatus == CANCELLED;
            case EXPIRED -> targetStatus == ACTIVE ||
                    targetStatus == CANCELLED;
            case PENDING_REVIEW -> targetStatus == ACTIVE ||
                    targetStatus == REVIEW_REJECTED ||
                    targetStatus == DISABLED ||
                    targetStatus == LOCKED;
            case REVIEW_REJECTED -> targetStatus == PENDING_REVIEW ||
                    targetStatus == DISABLED ||
                    targetStatus == LOCKED ||
                    targetStatus == CANCELLED;
            default -> false;
        };
    }

    public List<UserStatus> getAllowableTransitions() {
        return Arrays.stream(UserStatus.values())
                .filter(this::canTransitionTo)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        Collections::unmodifiableList
                ));
    }

    public UserStatus getRecommendedNextStatus() {
        return switch (this) {
            case PENDING, PENDING_REVIEW, LOCKED, EXPIRED -> ACTIVE;
            case REVIEW_REJECTED -> PENDING_REVIEW;
            default -> null;
        };
    }

    @Override
    public IEnumCode.EnumCodeItem<Integer, String> toEnumCodeItem() {
        return new IEnumCode.EnumCodeItem<>(value, label, code);
    }
}