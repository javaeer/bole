import {
  EmailBindingForm, MobileBindingForm,
  PasswordChangeForm,
  UserForm,
  UserInfo,
  UserPageQuery,
  UserPageVO,
  UserProfileForm,
  UserProfileVO,
} from "@/types/user";
import { request } from "@/utils/request";

const USER_BASE_URL = "/user";

const UserAPI = {
  /**
   * 获取当前登录用户信息
   *
   * @returns 登录用户昵称、头像信息，包括角色和权限
   */
  getUserInfo(): Promise<UserInfo> {
    return request.get<UserInfo>({
      url: `${USER_BASE_URL}/me`,
    });
  },

  /**
   * 获取用户分页列表
   *
   * @param queryParams 查询参数
   */
  getPage(queryParams: UserPageQuery) {
    return request.get<PageResult<UserPageVO[]>>({
      url: `${USER_BASE_URL}/page`,
      data: queryParams,
    });
  },
  /**
   * 添加用户
   *
   * @param data 用户表单数据
   */
  add(data: UserForm) {
    return request.post({
      url: `${USER_BASE_URL}`,
      data: data,
    });
  },

  /**
   * 获取用户表单详情
   *
   * @param userId 用户ID
   * @returns 用户表单详情
   */
  getFormData(userId: number) {
    return request.get<UserForm>({
      url: `${USER_BASE_URL}/${userId}/form`,
    });
  },

  /**
   * 修改用户
   *
   * @param id 用户ID
   * @param data 用户表单数据
   */
  update(id: number, data: UserForm) {
    return request.put({
      url: `${USER_BASE_URL}/${id}`,
      data: data,
    });
  },

  /** 获取个人中心用户信息 */
  getProfile() {
    return request.get<UserProfileVO>({
      url: `${USER_BASE_URL}/profile`,
    });
  },

  /** 修改个人中心用户信息 */
  updateProfile(data: UserProfileForm) {
    return request.put({
      url: `${USER_BASE_URL}/profile`,
      data: data,
    });
  },

  /** 修改个人中心用户密码 */
  changePassword(data: PasswordChangeForm) {
    return request.put({
      url: `${USER_BASE_URL}/password`,
      data: data,
    });
  },

  /**
   *   发送手机/邮箱验证码
   *
   * @param contact 联系方式  手机号/邮箱
   * @param contactType 联系方式类型 MOBILE:手机;EMAIL:邮箱
   */
  sendVerificationCode(contact: string, contactType: string) {
    return request.post({
      url: `${USER_BASE_URL}/send-verification-code?contact=${contact}&contactType=${contactType}`,
    });
  },

  /** 绑定个人中心用户手机 */
  bindMobile(data: MobileBindingForm) {
    return request.put({
      url: `${USER_BASE_URL}/mobile`,
      data: data,
    });
  },

  /** 绑定个人中心用户邮箱 */
  bindEmail(data: EmailBindingForm) {
    return request.put({
      url: `${USER_BASE_URL}/email`,
      data: data,
    });
  },

  /**
   * 批量删除用户，多个以英文逗号(,)分割
   *
   * @param ids 用户ID字符串，多个以英文逗号(,)分割
   */
  deleteByIds(ids: string) {
    return request.delete({
      url: `${USER_BASE_URL}/${ids}`,
    });
  },
};
export default UserAPI;

