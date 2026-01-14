export interface StyleObject {
  [key: string]: string | number | undefined;
}

export interface ThemeConfig {
  primaryColor?: string;
  secondaryColor?: string;
  successColor?: string;
  warningColor?: string;
  dangerColor?: string;
  infoColor?: string;
  textColor?: string;
  backgroundColor?: string;
  borderColor?: string;
  borderRadius?: string | number;
  fontSize?: string | number;
  spacing?: string | number;
}


export interface ResponsiveConfig {
  xs?: number;    // 手机
  sm?: number;    // 平板
  md?: number;    // 桌面
  lg?: number;    // 大屏
  xl?: number;    // 超大屏
}

export interface GradientConfig {
  from: string;
  to: string;
  direction?: "to right" | "to left" | "to bottom" | "to top" | "to bottom right" | "to bottom left" | "to top right" | "to top left";
  type?: "linear" | "radial";
}
