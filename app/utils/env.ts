// 环境配置工具
export class EnvConfig {
  /**
   * 获取基础 API URL
   */
  static getBaseURL(): string {
    return import.meta.env.VITE_APP_BASE_URL || this.getDefaultBaseURL();
  }
  
  /**
   * 获取 API 前缀
   */
  static getApiPrefix(): string {
    return import.meta.env.VITE_APP_BASE_API || '/api';
  }
  
  /**
   * 获取完整 API 地址
   */
  static getFullApiURL(path: string = ''): string {
    const baseURL = this.getBaseURL().replace(/\/$/, '');
    const apiPrefix = this.getApiPrefix().replace(/^\//, '').replace(/\/$/, '');
    const cleanPath = path.replace(/^\//, '');
    
    return `${baseURL}/${apiPrefix}/${cleanPath}`;
  }
  
  /**
   * 是否开发环境
   */
  static isDev(): boolean {
    return import.meta.env.VITE_APP_NODE_ENV === 'development' || 
           import.meta.env.DEV;
  }
  
  /**
   * 是否测试环境
   */
  static isTest(): boolean {
    return import.meta.env.VITE_APP_NODE_ENV === 'test';
  }
  
  /**
   * 是否生产环境
   */
  static isProd(): boolean {
    return import.meta.env.VITE_APP_NODE_ENV === 'production' || 
           import.meta.env.PROD;
  }
  
  /**
   * 获取应用标题
   */
  static getAppTitle(): string {
    return import.meta.env.VITE_APP_TITLE || 'Boole';
  }
  
  /**
   * 获取默认的基础 URL
   */
  private static getDefaultBaseURL(): string {
    if (this.isDev()) return 'http://localhost:3000';
    if (this.isTest()) return 'https://test-api.boole.com';
    return 'https://api.boole.com';
  }
}