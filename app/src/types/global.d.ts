declare global {

  /**
   * 响应数据
   */
  interface ResponseResult<T = any> {
    code: number;
    data: T;
    msg: string;
    timestamp: number;
  }

  interface TokenResult {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresIn: number;
    refreshExpiresIn: number | null;
  }

  /**
   * 分页查询参数
   */
  interface PageQuery {
    pageNum: number;
    pageSize: number;
    sortBy?: string;
    sortOrder?: "asc" | "desc";
  }

  /**
   * 分页响应对象
   */
  interface PageResult<T> {
    records: T[];
    total: number;
    size: number;
    current: number;
    pages: number;
  }

  /**
   * 组件数据源
   */
  interface OptionType {
    /** 值 */
    value: string | number;
    /** 文本 */
    label: string;
    /** 子列表  */
    children?: OptionType[];
  }


  /**
   * 请求配置
   */
  interface RequestConfig extends UniApp.RequestOptions {
    loading?: boolean;
    showError?: boolean;
  }

  // 自定义错误类
  class RequestError extends Error {
    code: number;
    data?: any;

    constructor(message: string, code: number, data?: any) {
      super(message);
      this.name = "RequestError";
      this.code = code;
      this.data = data;
    }
  }
}
export {};
