// 用户信息 - 确保包含所有必要的属性
export interface UserInfo {
  id: number;
  createdAt: string;
  updatedAt: string;
  deleted: number;
  companyId: number;
  username: string;
  email: string;
  phone: string | null;
  name: string | null;
  avatar: string | null;
  title: string | null;
  location: string | null;
  website: string | null;
  github: string | null;
  wechat: string | null;
  bio: string | null;
  followers: number;
  fans: number;
  likes: number;
  status: number;
  lastLoginAt: string;
  workYears: number;
  userRoles: any | null;
  authorities: Authority[];
  company: any | null;
}

export interface Authority {
  id: number;
  name: string;
  code: string;
}

/** 登录响应 */
export interface LoginResult extends TokenResult {
  userInfo: UserInfo;
}

export interface LoginForm {
  username: string;
  password: string;
}

export interface SmsLoginForm {
  phone: string;
  code: string;
}

export interface WechatLoginForm {
  code: string;
}

/** 修改个人信息 */
export interface UpdateForm {
  /**账号名 */
  username?: string;
  /**电子邮箱 */
  email?: string;
  /**手机号 */
  phone?: string;
  /**姓名 */
  name?: string;
  /**头像 */
  avatar?: string;
  /**头衔 */
  title?: string;
  /**企业 */
  company?: string;
  /**坐标 */
  location?: string;
  /**网站 */
  website?: string;
  /**GitHub */
  github?: string;
  /**微信号 */
  wechat?: string;
  /**BIO */
  bio?: string;
}

/** 修改密码表单 */
export interface PasswordChangeForm {
  /** 原密码 */
  oldPassword?: string;
  /** 新密码 */
  newPassword?: string;
  /** 确认新密码 */
  confirmPassword?: string;
}

/** 修改手机表单 */
export interface BindPhoneForm {
  /** 手机号 */
  phone?: string;
  /** 验证码 */
  code?: string;
  /** 密码 */
  password?: string;
}

/** 修改邮箱表单 */
export interface BindEmailForm {
  /** 邮箱 */
  email?: string;
  /** 验证码 */
  code?: string;
  /** 密码 */
  password?: string;
}

export interface ResetPasswordForm {
  type?: string;
  /**账号*/
  username?: string;
  /** 验证码 */
  code?: string;
  /** 新密码 */
  newPassword?: number;
}

/**
 * 用户注册表单
 */
export interface RegisterForm {
  username?: string;

  email?: string;
  emailCode?: string;

  areaCode?: string;
  phone?: string;
  code?: string;

  password: string;
  confirmPassword?: string;

  invitationCode?: string;
}

export interface PhoneRegisterForm {
  areaCode?: string;
  phone?: string;
  code?: string;
  password: string;
}

export interface EmailRegisterForm {
  email?: string;
  code?: string;
  password: string;
}

/**
 * 登录检查配置选项
 */
export interface LoginCheckOptions {
  /** 提示信息 */
  message?: string;
  /** 确认按钮文字 */
  confirmText?: string;
  /** 取消按钮文字 */
  cancelText?: string;
  /** 是否跳转登录页 */
  redirect?: boolean;
  /** 自定义登录路径 */
  loginPath?: string;
  /** 自定义失败回调 */
  onFail?: () => void;

  redirectPath?: any;
}

export interface LogoutOptions {
  callApi?: boolean;
  clearStorage?: boolean;
}


// 自定义错误类型
export class LoginError extends Error {
  constructor(
    message: string,
    public type: "not_logged_in" | "user_cancelled" | "redirect_failed" | "modal_failed" = "not_logged_in",
  ) {
    super(message);
    this.name = "LoginError";
  }
}