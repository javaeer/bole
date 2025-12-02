import { DictData, DictResult } from "@/types/dict";
import { StoreKey } from "@/constants/store-key";
import { LoginResult, UserInfo } from "@/types/user";
import { ConfigResult } from "@/types/config";


/**
 * 将配置列表存入本地存储
 */
export const setConfig = (configList: ConfigResult): void => {
  try {
    // 方法1：将整个配置数组存储
    uni.setStorageSync(StoreKey.SYSTEM_CONFIG_LIST, JSON.stringify(configList));
    console.log("系统配置已保存到本地存储");
  } catch (storageError) {
    console.error("保存配置到本地存储失败:", storageError);
  }
};

/**
 * 从本地存储加载配置
 */
export const getConfig = (): any => {
  try {
    const storedConfig = uni.getStorageSync(StoreKey.SYSTEM_CONFIG_LIST);
    if (storedConfig) {
      console.log("从本地存储加载配置成功");
      return storedConfig || null;
    }
  } catch (error) {
    console.error("从本地存储加载配置失败:", error);
  }
};

/**
 * 清除配置缓存
 */
export const clearConfig = (): void => {
  try {
    // 清除整个配置列表
    uni.removeStorageSync(StoreKey.SYSTEM_CONFIG_LIST);
    console.log("配置缓存已清除");
  } catch (error) {
    console.error("清除配置缓存失败:", error);
  }
};



/**
 * 获取本地存储中的字典数据
 */
export const getDict = (): DictData => {
  try {

    const storedDict = uni.getStorageSync(StoreKey.DICT_KEY);
    if (storedDict) {
      console.log("从本地加载字典数据成功");
      return storedDict ? JSON.parse(storedDict) : {};
    }
  } catch (error) {
    console.error("获取字典缓存失败:", error);
    return {};
  }
};

/**
 * 设置字典数据到本地存储
 */
export const setDict = (dictData: DictData): void => {
  try {
    uni.setStorageSync(StoreKey.DICT_KEY, JSON.stringify(dictData));
  } catch (error) {
    console.error("设置字典缓存失败:", error);
  }
};

/**
 * 清除字典缓存
 */
export const clearDict = (): void => {
  try {
    uni.removeStorageSync(StoreKey.DICT_KEY);
    console.log("字典缓存已清除");
  } catch (error) {
    console.error("清除字典缓存失败:", error);
  }
};


/**
 * 设置令牌信息
 * 如果包含 userInfo 则一起存储，否则只更新令牌
 */
export const setToken = (result: LoginResult | TokenResult): void => {
  const { accessToken, refreshToken, expiresIn, userInfo } = result;

  // 计算过期时间（当前时间 + 过期秒数 - 提前5分钟刷新）
  const expireTime = Date.now() + (expiresIn - 300) * 1000;

  uni.setStorageSync(StoreKey.TOKEN_KEY, accessToken);
  uni.setStorageSync(StoreKey.REFRESH_TOKEN_KEY, refreshToken);
  uni.setStorageSync(StoreKey.TOKEN_EXPIRE_KEY, expireTime.toString());

  if (userInfo !== null && userInfo !== undefined) {
    setUserInfo(userInfo);
  }
};
/**
 * 获取访问令牌
 */
export const getToken = (): string | null => {
  return uni.getStorageSync(StoreKey.TOKEN_KEY) || null;
};

/**
 * 获取刷新令牌
 */
export const getRefreshToken = (): string | null => {
  return uni.getStorageSync(StoreKey.REFRESH_TOKEN_KEY) || null;
};

/**
 * 检查令牌是否即将过期
 */
export const isTokenExpiring = (): boolean => {
  const expireTime = uni.getStorageSync(StoreKey.TOKEN_EXPIRE_KEY);
  if (!expireTime) return true;
  return Date.now() >= parseInt(expireTime);
};

/**
 * 检查是否有刷新令牌
 */
export const hasRefreshToken = (): boolean => {
  return !!getRefreshToken();
};

/**
 * 清除所有令牌
 */
export const clearToken = (): void => {
  uni.removeStorageSync(StoreKey.TOKEN_KEY);
  uni.removeStorageSync(StoreKey.REFRESH_TOKEN_KEY);
  uni.removeStorageSync(StoreKey.TOKEN_EXPIRE_KEY);
};

/**
 * 设置用户信息
 * @param userInfo
 */
export const setUserInfo = (userInfo: UserInfo) => {
  // uni.setStorageSync(StoreKey.USER_INFO_KEY, userInfo);
  uni.setStorageSync(StoreKey.USER_INFO_KEY, JSON.stringify(userInfo));
};

/**
 * 获取用户信息
 */
export const getUserInfo = (): any => {
  // return uni.getStorageSync(StoreKey.USER_INFO_KEY) || null;
  const userInfo = uni.getStorageSync(StoreKey.USER_INFO_KEY);
  if (userInfo){
    console.log("本地获取用户信息成功");
    return userInfo ? JSON.parse(userInfo) : null;
  }
};

/**
 * 清除用户信息
 */
export const clearUserInfo = (): void => {
  uni.removeStorageSync(StoreKey.USER_INFO_KEY);
};



// 清除用户所有缓存信息
export function clearUserAll() {
  clearToken();
  clearUserInfo();
}


// 清除所有缓存信息
export function clearAll() {
  clearToken();
  clearConfig();
  clearUserInfo();
  clearDict();
}