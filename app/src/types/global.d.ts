declare global {

  /**
   * 通用响应数据
   */
  interface ResponseResult<T = any> {
    code: number;
    data: T;
    message: string;
    timestamp: number;
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
   * 分页查询参数 将追加到 params
   */
  interface PageQuery {
    /*起始页 */
    page: number;
    /*每页行数 */
    size: number;
  }

  /**
   * 分页组件 状态 存储前端分页组件的状态
   */
  interface PaginationState {
    current: number;   // 当前页码
    pageSize: number;  // 每页大小
    total: number;     // 总条数
    pages: number;     // 总页数
  }

  /**
   * 查询条件 将实例化到 body中
   */
  interface BodyParams {
    /*查询开始于 */
    queryStartAt?: string;
    /*查询止于 */
    queryStopAt?: string;
    /*关键字 */
    keyWords?: string;
    /*查询行 */
    keyField?: string;
    /*排序行 */
    sortBy?: string;
    /*查询方式 */
    sortOrder?: "asc" | "desc";
  }

  /**
   * 控制数据加载行为的参数对象
   */
  interface LoadParams {
    page?: number;           // 页码
    size?: number;          // 每页数量
    query?: BodyParams & Record<string, any>;  // 查询条件
    append?: boolean;       // 是否追加数据（用于加载更多）
    showToast?: boolean;    // 是否显示错误提示
    sortBy?: string;        // 排序字段
    sortOrder?: "asc" | "desc"; // 排序方向
  }

  /**
   * 缓存中的 查询参数
   */
  interface CacheKeyParams {
    page: number;
    size: number;
    query: BodyParams & Record<string, any>;
    sortBy?: string;
    sortOrder?: "asc" | "desc";
  }


  /**
   * Token 相关响应数据
   */
  interface TokenResult {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresIn: number;
    refreshExpiresIn: number | null;
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
   * 客户端数据
   */
  interface DeviceOption {
    value: string;
    name: string;
    icon: string;
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
