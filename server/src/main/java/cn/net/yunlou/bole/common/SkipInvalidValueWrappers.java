package cn.net.yunlou.bole.common;

/**
 * 自定义 Wrappers 工具类，提供忽略无效值的包装器
 * 简化包装器的创建，提供统一的入口
 *
 *
 * // 基础使用
 * SkipInvalidValueLambdaQueryWrapper<User> queryWrapper =
 *     SkipInvalidValueWrappers.lambdaQuery(user);
 *
 * SkipInvalidValueLambdaUpdateWrapper<User> updateWrapper =
 *     SkipInvalidValueWrappers.lambdaUpdate(user);
 *
 * // 严格模式（调试用）
 * SkipInvalidValueLambdaQueryWrapper<User> strictQueryWrapper =
 *     SkipInvalidValueWrappers.strictLambdaQuery(user);
 *
 * // 允许null值
 * SkipInvalidValueLambdaUpdateWrapper<User> lenientUpdateWrapper =
 *     SkipInvalidValueWrappers.lenientLambdaUpdate(user);
 *
 * // 空构造
 * SkipInvalidValueLambdaQueryWrapper<User> emptyQueryWrapper =
 *     SkipInvalidValueWrappers.lambdaQuery();
 *
 * // 基于类名构造
 * SkipInvalidValueLambdaQueryWrapper<User> classQueryWrapper =
 *     SkipInvalidValueWrappers.lambdaQuery(User.class);
 *
 * // 组合使用
 * SkipInvalidValueLambdaQueryWrapper<User> combinedWrapper =
 *     SkipInvalidValueWrappers.strictAndLenientLambdaQuery(user);
 *
 */
public class SkipInvalidValueWrappers {

    private SkipInvalidValueWrappers() {
        // 工具类，防止实例化
    }

    // ============ Query Wrapper 方法 ============

    /**
     * 获取忽略无效值的 QueryWrapper（基于实体）
     */
    public static <T> SkipInvalidValueQueryWrapper<T> query(T entity) {
        return SkipInvalidValueQueryWrapper.of(entity);
    }

    /**
     * 获取忽略无效值的 QueryWrapper（基于实体类）
     */
    public static <T> SkipInvalidValueQueryWrapper<T> query(Class<T> entityClass) {
        return SkipInvalidValueQueryWrapper.of(entityClass);
    }

    /**
     * 获取忽略无效值的 QueryWrapper（空构造）
     */
    public static <T> SkipInvalidValueQueryWrapper<T> query() {
        return SkipInvalidValueQueryWrapper.create();
    }

    // ============ Lambda Query Wrapper 方法 ============

    /**
     * 获取忽略无效值的 LambdaQueryWrapper（基于实体）
     */
    public static <T> SkipInvalidValueLambdaQueryWrapper<T> lambdaQuery(T entity) {
        return SkipInvalidValueLambdaQueryWrapper.of(entity);
    }

    /**
     * 获取忽略无效值的 LambdaQueryWrapper（基于实体类）
     */
    public static <T> SkipInvalidValueLambdaQueryWrapper<T> lambdaQuery(Class<T> entityClass) {
        return SkipInvalidValueLambdaQueryWrapper.of(entityClass);
    }

    /**
     * 获取忽略无效值的 LambdaQueryWrapper（空构造）
     */
    public static <T> SkipInvalidValueLambdaQueryWrapper<T> lambdaQuery() {
        return SkipInvalidValueLambdaQueryWrapper.create();
    }

    // ============ Update Wrapper 方法 ============

    /**
     * 获取忽略无效值的 UpdateWrapper（基于实体）
     */
    public static <T> SkipInvalidValueUpdateWrapper<T> update(T entity) {
        return SkipInvalidValueUpdateWrapper.of(entity);
    }

    /**
     * 获取忽略无效值的 UpdateWrapper（基于实体类）
     */
    public static <T> SkipInvalidValueUpdateWrapper<T> update(Class<T> entityClass) {
        return SkipInvalidValueUpdateWrapper.of(entityClass);
    }

    /**
     * 获取忽略无效值的 LambdaUpdateWrapper（空构造）
     */
    public static <T> SkipInvalidValueUpdateWrapper<T> update() {
        return SkipInvalidValueUpdateWrapper.create();
    }

    // ============ Lambda Update Wrapper 方法 ============

    /**
     * 获取忽略无效值的 LambdaUpdateWrapper（基于实体）
     */
    public static <T> SkipInvalidValueLambdaUpdateWrapper<T> lambdaUpdate(T entity) {
        return SkipInvalidValueLambdaUpdateWrapper.of(entity);
    }

    /**
     * 获取忽略无效值的 LambdaUpdateWrapper（基于实体类）
     */
    public static <T> SkipInvalidValueLambdaUpdateWrapper<T> lambdaUpdate(Class<T> entityClass) {
        return SkipInvalidValueLambdaUpdateWrapper.of(entityClass);
    }

    /**
     * 获取忽略无效值的 LambdaUpdateWrapper（空构造）
     */
    public static <T> SkipInvalidValueLambdaUpdateWrapper<T> lambdaUpdate() {
        return SkipInvalidValueLambdaUpdateWrapper.create();
    }

    // ============ 快捷方法 - 启用严格模式 ============

    /**
     * 获取严格模式的 LambdaQueryWrapper（基于实体）
     */
    public static <T> SkipInvalidValueLambdaQueryWrapper<T> strictLambdaQuery(T entity) {
        return SkipInvalidValueLambdaQueryWrapper.of(entity).strictMode();
    }

    /**
     * 获取严格模式的 LambdaQueryWrapper（基于实体类）
     */
    public static <T> SkipInvalidValueLambdaQueryWrapper<T> strictLambdaQuery(Class<T> entityClass) {
        return SkipInvalidValueLambdaQueryWrapper.of(entityClass).strictMode();
    }

    /**
     * 获取严格模式的 LambdaQueryWrapper（空构造）
     */
    public static <T> SkipInvalidValueLambdaQueryWrapper<Object> strictLambdaQuery() {
        return SkipInvalidValueLambdaQueryWrapper.create().strictMode();
    }

    /**
     * 获取严格模式的 LambdaUpdateWrapper（基于实体）
     */
    public static <T> SkipInvalidValueLambdaUpdateWrapper<T> strictLambdaUpdate(T entity) {
        return SkipInvalidValueLambdaUpdateWrapper.of(entity).strictMode();
    }

    /**
     * 获取严格模式的 LambdaUpdateWrapper（基于实体类）
     */
    public static <T> SkipInvalidValueLambdaUpdateWrapper<T> strictLambdaUpdate(Class<T> entityClass) {
        return SkipInvalidValueLambdaUpdateWrapper.of(entityClass).strictMode();
    }

    /**
     * 获取严格模式的 LambdaUpdateWrapper（空构造）
     */
    public static <T> SkipInvalidValueLambdaUpdateWrapper<Object> strictLambdaUpdate() {
        return SkipInvalidValueLambdaUpdateWrapper.create().strictMode();
    }

    // ============ 快捷方法 - 允许Null值 ============

    /**
     * 获取允许Null值的 LambdaQueryWrapper（基于实体）
     */
    public static <T> SkipInvalidValueLambdaQueryWrapper<T> lenientLambdaQuery(T entity) {
        return SkipInvalidValueLambdaQueryWrapper.of(entity).allowNullValue();
    }

    /**
     * 获取允许Null值的 LambdaUpdateWrapper（基于实体）
     */
    public static <T> SkipInvalidValueLambdaUpdateWrapper<T> lenientLambdaUpdate(T entity) {
        return SkipInvalidValueLambdaUpdateWrapper.of(entity).allowNullValue();
    }

    // ============ 组合方法 ============

    /**
     * 获取严格模式且允许Null值的 LambdaQueryWrapper
     */
    public static <T> SkipInvalidValueLambdaQueryWrapper<T> strictAndLenientLambdaQuery(T entity) {
        return SkipInvalidValueLambdaQueryWrapper.of(entity)
                .strictMode()
                .allowNullValue();
    }

    /**
     * 获取严格模式且允许Null值的 LambdaUpdateWrapper
     */
    public static <T> SkipInvalidValueLambdaUpdateWrapper<T> strictAndLenientLambdaUpdate(T entity) {
        return SkipInvalidValueLambdaUpdateWrapper.of(entity)
                .strictMode()
                .allowNullValue();
    }
}