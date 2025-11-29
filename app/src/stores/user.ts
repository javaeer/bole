import { defineStore } from "pinia";
import AuthAPI from "@/api/auth";
import UserAPI from "@/api/user";
import { clearAll, getUserInfo, setToken, setUserInfo } from "@/utils/store";
import { LoginFormData, UserInfo } from "@/types/user";

export const useUserStore = defineStore("user", () => {
  const userInfo = ref<UserInfo | undefined>(getUserInfo());

  // 登录
  const login = (data: LoginFormData) => {
    return new Promise((resolve, reject) => {
      AuthAPI.login(data)
        .then((data) => {
          console.log(data);
          setToken(data.accessToken);
          setUserInfo(data.userInfo);
          resolve(data);
        })
        .catch((error) => {
          console.error("登录失败", error);
          reject(error);
        });
    });
  };

  // 微信登录
  const loginByWechat = (code: string) => {
    return new Promise((resolve, reject) => {
      AuthAPI.wechatLogin(code)
        .then((data) => {
          setToken(data.accessToken);
          resolve(data);
        })
        .catch((error) => {
          console.error("微信登录失败", error);
          reject(error);
        });
    });
  };

  // 获取用户信息
  const getInfo = () => {
    return new Promise((resolve, reject) => {
      UserAPI.getUserInfo()
        .then((data) => {
          setUserInfo(data);
          userInfo.value = data;
          resolve(data);
        })
        .catch((error) => {
          console.error("获取用户信息失败", error);
          reject(error);
        });
    });
  };

  // 登出
  const logout = async () => {
    try {
      await AuthAPI.logout(); // 调用后台注销接口
    } catch (error) {
      console.error("登出失败", error);
    } finally {
      clearAll(); // 清除本地的 token 和用户信息缓存
      userInfo.value = undefined; // 清空用户信息
    }
  };

  return {
    userInfo,
    login,
    loginByWechat,
    logout,
    getInfo,
  };
});