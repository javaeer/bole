/** 系统配置项 */
export interface ConfigItem {
  configKey: string;
  configValue: string;
  configDesc: string;
}

/** 系统配置响应 */
export type ConfigResult = ConfigItem[];