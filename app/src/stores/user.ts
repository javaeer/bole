import { computed, ref } from "vue";
import { defineStore } from "pinia";
import { UserInfo } from "@/types/user";
import { getToken, getUserInfo as getStoredUserInfo } from "@/utils/store";

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
    addRefreshSubscriber,
    onRefreshed,
    setRefreshing,
    getRefreshToken,
  };
});