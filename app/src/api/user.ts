import { BindEmailForm, BindPhoneForm, PasswordChangeForm, UpdateForm, UserInfo } from "@/types/user";
import { request } from "@/utils/request";

const USER_BASE_URL = "/user";

const UserAPI = {
  /**
   * 获取当前登录用户信息
   *
   * @returns 登录用户昵称、头像信息，包括角色和权限
   */
  async getUserInfo(): Promise<UserInfo> {
    return request.get<UserInfo>(`${USER_BASE_URL}/me`);
  },


  /** 修改个人中心用户信息 */
  async updateProfile(data: UpdateForm): Promise<UserInfo> {
    return request.put<UserInfo>(`${USER_BASE_URL}/profile`, data);
  },

  /** 修改个人中心用户密码 */
  changePassword(data: PasswordChangeForm) {
    return request.put(`${USER_BASE_URL}/password`, data);
  },


  /** 绑定个人中心用户手机 */
  bindPhone(data: BindPhoneForm) {
    return request.put(`${USER_BASE_URL}/bind/phone`, data);
  },

  /** 解绑个人中心用户手机 */
  unbindPhone(data: BindPhoneForm) {
    return request.put(`${USER_BASE_URL}/unbind/phone`, data);
  },

  /** 绑定个人中心用户邮箱 */
  bindEmail(data: BindEmailForm) {
    return request.put(`${USER_BASE_URL}/bind/email`, data);
  },

  /** 绑定个人中心用户邮箱 */
  unbindEmail(data: BindEmailForm) {
    return request.put(`${USER_BASE_URL}/unbind/email`, data);
  },

};
export default UserAPI;

