// src/stores/user-store.ts
import { ref, computed } from 'vue';
import { defineStore } from 'pinia';
import { LoginResult, UserInfo } from '@/types/user';
import { getToken, getRefreshToken, setToken, clearAll, getUserInfo as getStoredUserInfo } from '@/utils/store';

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string | null>(getToken());
  const userInfo = ref<UserInfo | null>(getStoredUserInfo());
  const isRefreshing = ref(false);
  const refreshSubscribers = ref<Array<(token: string) => void>>([]);

  // Getter
  const isLoggedIn = computed(() => !!token.value);
  const currentUser = computed(() => userInfo.value);

  // Actions
  const setUserData = (loginResult: LoginResult) => {
    const { accessToken, userInfo: newUserInfo } = loginResult;
    token.value = accessToken;

    // 只有当 userInfo 存在时才更新
    if (newUserInfo !== null && newUserInfo !== undefined) {
      userInfo.value = newUserInfo;
    }

    // 存储到本地存储
    setToken(loginResult);
  };

  const clearUserData = () => {
    token.value = null;
    userInfo.value = null;
    clearAll();
  };

  const updateUserInfo = (newUserInfo: UserInfo) => {
    userInfo.value = newUserInfo;
    // 更新本地存储的用户信息
    setUserInfo(newUserInfo);
  };

  // 刷新令牌相关
  const addRefreshSubscriber = (callback: (token: string) => void) => {
    refreshSubscribers.value.push(callback);
  };

  const onRefreshed = (newToken: string) => {
    refreshSubscribers.value.forEach(callback => callback(newToken));
    refreshSubscribers.value = [];
  };

  const setRefreshing = (refreshing: boolean) => {
    isRefreshing.value = refreshing;
  };

  const getRefreshToken = () => {
    return getRefreshToken();
  };

  return {
    // 状态
    token,
    userInfo,
    isRefreshing,
    refreshSubscribers,

    // Getter
    isLoggedIn,
    currentUser,

    // Actions
    setUserData,
    clearUserData,
    updateUserInfo,
    addRefreshSubscriber,
    onRefreshed,
    setRefreshing,
    getRefreshToken,
  };
});