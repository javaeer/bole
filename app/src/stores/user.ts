import { computed, ref } from "vue";
import { defineStore } from "pinia";
import { LoginForm, UserInfo } from "@/types/user";
import {
  clearAll,
  clearUserAll,
  getRefreshToken,
  getToken,
  getUserInfo,
  setToken as setTokenToStorage,
} from "@/utils/store";
import AuthAPI from "@/api/auth";

export const useUserStore = defineStore("user", () => {
  // 状态
  const token = ref<string | null>(getToken());
  const refreshToken = ref<string | null>(getRefreshToken());
  const userInfo = ref<UserInfo | null>(getUserInfo());
  const isRefreshing = ref(false);
  const refreshSubscribers = ref<Array<(token: string) => void>>([]);

  // Getter
  const isLoggedIn = computed(() => !!token.value);
  const currentUser = computed(() => userInfo.value);
  const authHeader = computed(() => ({
    Authorization: token.value ? `Bearer ${token.value}` : ""
  }));

  // Actions
  const updateToken = (newToken: string | null) => {
    token.value = newToken;
  };

  const updateRefreshToken = (newRefreshToken: string | null) => {
    refreshToken.value = newRefreshToken;
  };

  const updateUserInfo = (newUserInfo: UserInfo | null) => {
    userInfo.value = newUserInfo;
  };

  /**
   * 登录
   */
  const login = async (credentials: LoginForm) => {
    try {
      const result = await AuthAPI.login(credentials);
      // 更新本地存储
      setTokenToStorage(result);
      // 更新store状态
      token.value = result.accessToken;
      refreshToken.value = result.refreshToken;
      userInfo.value = result.userInfo || null;

      return result;
    } catch (error) {
      clearUserAll();
      // 同时清除store状态
      token.value = null;
      refreshToken.value = null;
      userInfo.value = null;
      throw error;
    }
  };

  /**
   * 静默登录
   */
  const silentLogin = async () => {
    if (!refreshToken.value) {
      throw new Error("无有效 refreshToken");
    }

    try {
      const result = await AuthAPI.refreshToken({
        refresh_token: refreshToken.value,
      });

      setTokenToStorage(result);
      token.value = result.accessToken;
      refreshToken.value = result.refreshToken;
      if (result.userInfo) {
        userInfo.value = result.userInfo;
      }

      return result;
    } catch (error) {
      clearUserAll();
      token.value = null;
      refreshToken.value = null;
      userInfo.value = null;
      throw error;
    }
  };

  /**
   * 登出
   */
  const logout = async (options: {
    callApi?: boolean;
    clearStorage?: boolean;
  } = { callApi: true, clearStorage: true }) => {
    const { callApi, clearStorage } = options;

    try {
      if (callApi && token.value) {
        await AuthAPI.logout();
      }
    } catch (error) {
      console.warn("登出接口调用失败:", error);
    } finally {
      if (clearStorage) {
        clearAll();
      } else {
        clearUserAll();
      }
      // 更新store状态
      token.value = null;
      refreshToken.value = null;
      userInfo.value = null;
    }
  };

  /**
   * 刷新 Token
   */
  const refreshTokenAction = async (): Promise<string> => {
    // 如果已经在刷新，返回 Promise 并订阅
    if (isRefreshing.value) {
      return new Promise((resolve) => {
        addRefreshSubscriber((newToken: string) => {
          resolve(newToken);
        });
      });
    }

    if (!refreshToken.value) {
      throw new Error("无法刷新 token: refreshToken 不存在");
    }

    isRefreshing.value = true;

    try {
      const result = await AuthAPI.refreshToken({
        refresh_token: refreshToken.value,
      });

      // 更新所有token
      setTokenToStorage(result);
      token.value = result.accessToken;
      refreshToken.value = result.refreshToken;

      // 通知所有订阅者
      onRefreshed(result.accessToken);

      return result.accessToken;
    } catch (error) {
      // 刷新失败，需要重新登录
      clearUserAll();
      token.value = null;
      refreshToken.value = null;
      userInfo.value = null;
      throw error;
    } finally {
      isRefreshing.value = false;
    }
  };

  // 刷新令牌相关
  const addRefreshSubscriber = (callback: (token: string) => void) => {
    refreshSubscribers.value.push(callback);
  };

  const onRefreshed = (newToken: string) => {
    refreshSubscribers.value.forEach(callback => callback(newToken));
    refreshSubscribers.value = [];
  };

  return {
    // 状态
    token,
    refreshToken,
    userInfo,
    isRefreshing,

    // Getter
    isLoggedIn,
    currentUser,
    authHeader,

    // Actions
    login,
    logout,
    silentLogin,
    refreshTokenAction,

    // 状态更新方法
    updateToken,
    updateRefreshToken,
    updateUserInfo,

    // 订阅方法
    addRefreshSubscriber,
  };
});