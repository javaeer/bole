import { GradientConfig, ResponsiveConfig, StyleObject, ThemeConfig } from "@/types/theme";
import { RESPONSIVE_CONFIG, THEME_CONFIG } from "@/constants/theme";

/**
 * 样式处理工具库
 * 针对 uniapp + Vue3 场景优化
 */
export class StyleUtils {
  // 默认主题配置
  private static defaultTheme: ThemeConfig = THEME_CONFIG;

  // 响应式断点配置
  private static responsiveBreakpoints: ResponsiveConfig = RESPONSIVE_CONFIG;

  // 当前主题
  private static currentTheme: ThemeConfig = { ...this.defaultTheme };

  /**
   * 设置主题
   */
  static setTheme(config: Partial<ThemeConfig>): void {
    this.currentTheme = { ...this.defaultTheme, ...config };
  }

  /**
   * 获取主题变量
   */
  static getThemeVar(key: keyof ThemeConfig): string | number {
    return this.currentTheme[key] || "";
  }

  /**
   * 获取所有主题变量
   */
  static getThemeVars(): Record<string, string | number> {
    const vars: Record<string, string | number> = {};
    Object.entries(this.currentTheme).forEach(([key, value]) => {
      vars[`--theme-${key}`] = value;
    });
    return vars;
  }

  /**
   * 将主题变量转换为 CSS 变量字符串
   */
  static getThemeCssVars(): string {
    return Object.entries(this.currentTheme)
      .map(([key, value]) => `--theme-${key}: ${value};`)
      .join(" ");
  }

  /**
   * 单位转换
   * uniapp 中 rpx 是响应式像素，750rpx = 屏幕宽度
   */
  static rpx(px: number): string {
    return `${px}rpx`;
  }

  /**
   * 像素转换
   */
  static px(value: number): string {
    return `${value}px`;
  }

  /**
   * 百分比转换
   */
  static percent(value: number): string {
    return `${value}%`;
  }

  /**
   * 视口宽度转换 (vw)
   */
  static vw(value: number): string {
    return `${value}vw`;
  }

  /**
   * 视口高度转换 (vh)
   */
  static vh(value: number): string {
    return `${value}vh`;
  }

  /**
   * 合并样式对象
   * 支持对象、字符串、数组等多种格式
   */
  static mergeStyles(...styles: Array<StyleObject | string | undefined | null>): StyleObject {
    const result: StyleObject = {};

    styles.forEach(style => {
      if (!style) return;

      if (typeof style === "string") {
        // 解析字符串样式
        const stylePairs = style.split(";").filter(pair => pair.trim());
        stylePairs.forEach(pair => {
          const [key, value] = pair.split(":").map(s => s.trim());
          if (key && value) {
            result[key] = value;
          }
        });
      } else if (Array.isArray(style)) {
        // 处理数组
        const merged = this.mergeStyles(...style);
        Object.assign(result, merged);
      } else if (typeof style === "object") {
        // 合并对象
        Object.assign(result, style);
      }
    });

    return result;
  }

  /**
   * 样式对象转字符串
   */
  static styleToString(styleObj: StyleObject): string {
    return Object.entries(styleObj)
      .filter(([_, value]) => value !== undefined && value !== null && value !== "")
      .map(([key, value]) => {
        // 转换驼峰为连字符
        const cssKey = key.replace(/[A-Z]/g, match => `-${match.toLowerCase()}`);
        return `${cssKey}: ${value};`;
      })
      .join(" ");
  }

  /**
   * 创建 CSS 类名（类似 classnames 库）
   */
  static classNames(...args: any[]): string {
    const classes: string[] = [];

    args.forEach(arg => {
      if (!arg) return;

      if (typeof arg === "string") {
        classes.push(arg);
      } else if (typeof arg === "object") {
        if (Array.isArray(arg)) {
          classes.push(this.classNames(...arg));
        } else {
          Object.entries(arg).forEach(([key, value]) => {
            if (value) {
              classes.push(key);
            }
          });
        }
      }
    });

    return classes.join(" ");
  }

  /**
   * 生成响应式样式
   */
  static responsive<T>(config: {
    xs?: T;
    sm?: T;
    md?: T;
    lg?: T;
    xl?: T;
  }, transform: (value: T) => string): Record<string, string> {
    const result: Record<string, string> = {};

    Object.entries(config).forEach(([breakpoint, value]) => {
      if (value !== undefined) {
        const bp = this.responsiveBreakpoints[breakpoint as keyof ResponsiveConfig];
        if (bp !== undefined) {
          if (bp === 0) {
            // 基础样式
            result["...base"] = transform(value);
          } else {
            // 媒体查询
            result[`@media (min-width: ${bp}px)`] = transform(value);
          }
        }
      }
    });

    return result;
  }

  /**
   * 生成渐变背景
   */
  static gradient(config: GradientConfig): string {
    const { from, to, direction = "to right", type = "linear" } = config;

    if (type === "linear") {
      return `linear-gradient(${direction}, ${from}, ${to})`;
    } else {
      return `radial-gradient(circle, ${from}, ${to})`;
    }
  }

  /**
   * 颜色处理 - 透明度
   */
  static alpha(color: string, opacity: number): string {
    // 处理十六进制颜色
    if (color.startsWith("#")) {
      const hex = color.slice(1);
      let r = 0, g = 0, b = 0;

      if (hex.length === 3) {
        r = parseInt(hex[0] + hex[0], 16);
        g = parseInt(hex[1] + hex[1], 16);
        b = parseInt(hex[2] + hex[2], 16);
      } else if (hex.length === 6) {
        r = parseInt(hex.slice(0, 2), 16);
        g = parseInt(hex.slice(2, 4), 16);
        b = parseInt(hex.slice(4, 6), 16);
      }

      return `rgba(${r}, ${g}, ${b}, ${opacity})`;
    }

    // 处理 rgb/rgba 颜色
    if (color.startsWith("rgb")) {
      const match = color.match(/(\d+),\s*(\d+),\s*(\d+)/);
      if (match) {
        const [_, r, g, b] = match;
        return `rgba(${r}, ${g}, ${b}, ${opacity})`;
      }
    }

    return color;
  }

  /**
   * 颜色处理 - 变亮
   */
  static lighten(color: string, amount: number): string {
    return this.adjustColor(color, amount, "lighten");
  }

  /**
   * 颜色处理 - 变暗
   */
  static darken(color: string, amount: number): string {
    return this.adjustColor(color, amount, "darken");
  }

  /**
   * 颜色调整
   */
  private static adjustColor(color: string, amount: number, operation: "lighten" | "darken"): string {
    // 简化的颜色调整实现
    // 实际项目中可能需要更精确的颜色处理
    if (color.startsWith("#")) {
      const hex = color.slice(1);
      let r = 0, g = 0, b = 0;

      if (hex.length === 3) {
        r = parseInt(hex[0] + hex[0], 16);
        g = parseInt(hex[1] + hex[1], 16);
        b = parseInt(hex[2] + hex[2], 16);
      } else if (hex.length === 6) {
        r = parseInt(hex.slice(0, 2), 16);
        g = parseInt(hex.slice(2, 4), 16);
        b = parseInt(hex.slice(4, 6), 16);
      }

      const adjust = (value: number) => {
        if (operation === "lighten") {
          return Math.min(255, value + Math.floor((255 - value) * amount));
        } else {
          return Math.max(0, value - Math.floor(value * amount));
        }
      };

      r = adjust(r);
      g = adjust(g);
      b = adjust(b);

      return `#${r.toString(16).padStart(2, "0")}${g.toString(16).padStart(2, "0")}${b.toString(16).padStart(2, "0")}`;
    }

    return color;
  }

  /**
   * 生成阴影
   */
  static shadow(
    x: number = 0,
    y: number = 2,
    blur: number = 4,
    spread: number = 0,
    color: string = "rgba(0, 0, 0, 0.1)",
  ): string {
    return `${x}px ${y}px ${blur}px ${spread}px ${color}`;
  }

  /**
   * 生成盒阴影（多层）
   */
  static shadowLayers(level: 0 | 1 | 2 | 3 | 4 | 5): string {
    const shadows = [
      "none",
      "0 1px 2px rgba(0, 0, 0, 0.05)",
      "0 4px 6px rgba(0, 0, 0, 0.07), 0 1px 3px rgba(0, 0, 0, 0.06)",
      "0 10px 15px rgba(0, 0, 0, 0.1), 0 4px 6px rgba(0, 0, 0, 0.05)",
      "0 20px 25px rgba(0, 0, 0, 0.15), 0 10px 10px rgba(0, 0, 0, 0.05)",
      "0 25px 50px rgba(0, 0, 0, 0.25), 0 15px 15px rgba(0, 0, 0, 0.15)",
    ];

    return shadows[level] || shadows[0];
  }

  /**
   * 生成动画关键帧
   */
  static keyframes(name: string, frames: Record<string, StyleObject>): string {
    const frameStrings = Object.entries(frames)
      .map(([key, style]) => {
        const styleString = this.styleToString(style);
        return `${key} { ${styleString} }`;
      })
      .join(" ");

    return `@keyframes ${name} { ${frameStrings} }`;
  }

  /**
   * 生成动画简写属性
   */
  static animation(
    name: string,
    duration: string = "0.3s",
    timing: string = "ease",
    delay: string = "0s",
    iteration: string = "1",
    direction: string = "normal",
    fill: string = "none",
  ): string {
    return `${name} ${duration} ${timing} ${delay} ${iteration} ${direction} ${fill}`;
  }

  /**
   * 生成过渡效果
   */
  static transition(
    property: string = "all",
    duration: string = "0.3s",
    timing: string = "ease",
    delay: string = "0s",
  ): string {
    return `${property} ${duration} ${timing} ${delay}`;
  }

  /**
   * 生成 flex 布局样式
   */
  static flex(
    direction: "row" | "column" | "row-reverse" | "column-reverse" = "row",
    justify: "flex-start" | "flex-end" | "center" | "space-between" | "space-around" | "space-evenly" = "flex-start",
    align: "flex-start" | "flex-end" | "center" | "stretch" | "baseline" = "stretch",
    wrap: "nowrap" | "wrap" | "wrap-reverse" = "nowrap",
  ): StyleObject {
    return {
      display: "flex",
      flexDirection: direction,
      justifyContent: justify,
      alignItems: align,
      flexWrap: wrap,
    };
  }

  /**
   * 生成网格布局样式
   */
  static grid(
    columns: string | number = 1,
    rows?: string | number,
    gap?: string | number,
    justify?: string,
    align?: string,
  ): StyleObject {
    const style: StyleObject = {
      display: "grid",
      gridTemplateColumns: typeof columns === "number" ? `repeat(${columns}, 1fr)` : columns,
    };

    if (rows !== undefined) {
      style.gridTemplateRows = typeof rows === "number" ? `repeat(${rows}, 1fr)` : rows;
    }

    if (gap !== undefined) {
      style.gap = typeof gap === "number" ? `${gap}px` : gap;
    }

    if (justify !== undefined) {
      style.justifyContent = justify;
    }

    if (align !== undefined) {
      style.alignItems = align;
    }

    return style;
  }

  /**
   * 生成常用工具类
   */
  static utility = {
    // 间距工具
    margin: (value: string | number) => ({ margin: value }),
    marginX: (value: string | number) => ({ marginLeft: value, marginRight: value }),
    marginY: (value: string | number) => ({ marginTop: value, marginBottom: value }),
    marginTop: (value: string | number) => ({ marginTop: value }),
    marginBottom: (value: string | number) => ({ marginBottom: value }),
    marginLeft: (value: string | number) => ({ marginLeft: value }),
    marginRight: (value: string | number) => ({ marginRight: value }),

    padding: (value: string | number) => ({ padding: value }),
    paddingX: (value: string | number) => ({ paddingLeft: value, paddingRight: value }),
    paddingY: (value: string | number) => ({ paddingTop: value, paddingBottom: value }),
    paddingTop: (value: string | number) => ({ paddingTop: value }),
    paddingBottom: (value: string | number) => ({ paddingBottom: value }),
    paddingLeft: (value: string | number) => ({ paddingLeft: value }),
    paddingRight: (value: string | number) => ({ paddingRight: value }),

    // 尺寸工具
    width: (value: string | number) => ({ width: value }),
    height: (value: string | number) => ({ height: value }),
    maxWidth: (value: string | number) => ({ maxWidth: value }),
    maxHeight: (value: string | number) => ({ maxHeight: value }),
    minWidth: (value: string | number) => ({ minWidth: value }),
    minHeight: (value: string | number) => ({ minHeight: value }),

    // 文本工具
    textColor: (color: string) => ({ color }),
    fontSize: (size: string | number) => ({ fontSize: size }),
    fontWeight: (weight: string | number) => ({ fontWeight: weight }),
    textAlign: (align: "left" | "center" | "right" | "justify") => ({ textAlign: align }),
    lineHeight: (height: string | number) => ({ lineHeight: height }),

    // 背景工具
    bgColor: (color: string) => ({ backgroundColor: color }),
    bgGradient: (config: GradientConfig) => ({ backgroundImage: this.gradient(config) }),

    // 边框工具
    border: (width: string | number = "1px", style: string = "solid", color?: string) => {
      const borderColor = color || this.currentTheme.borderColor;
      return { border: `${width} ${style} ${borderColor}` };
    },
    borderRadius: (radius: string | number = this.currentTheme.borderRadius) => ({ borderRadius: radius }),

    // 显示工具
    display: (value: string) => ({ display: value }),
    visibility: (value: string) => ({ visibility: value }),
    opacity: (value: number) => ({ opacity: value }),

    // 定位工具
    position: (value: string) => ({ position: value }),
    top: (value: string | number) => ({ top: value }),
    right: (value: string | number) => ({ right: value }),
    bottom: (value: string | number) => ({ bottom: value }),
    left: (value: string | number) => ({ left: value }),
    zIndex: (value: number) => ({ zIndex: value }),

    // 溢出工具
    overflow: (value: string) => ({ overflow: value }),
    overflowX: (value: string) => ({ overflowX: value }),
    overflowY: (value: string) => ({ overflowY: value }),
  };

  /**
   * 生成 uniapp 特定样式
   */
  static uniapp = {
    // 安全区域适配（iPhone X+ 等）
    safeArea: (position: "top" | "bottom" | "both" = "both"): StyleObject => {
      const styles: StyleObject = {};

      if (position === "top" || position === "both") {
        styles.paddingTop = "var(--status-bar-height, 0)";
      }

      if (position === "bottom" || position === "both") {
        styles.paddingBottom = "env(safe-area-inset-bottom, 0)";
      }

      return styles;
    },

    // 适配导航栏高度
    navBarHeight: (): StyleObject => ({
      height: "var(--window-top, 44px)",
    }),

    // 固定底部（适合 tabbar）
    fixedBottom: (): StyleObject => ({
      position: "fixed",
      left: 0,
      right: 0,
      bottom: 0,
      "z-index": 999,
    }),

    // 固定顶部（适合导航栏）
    fixedTop: (): StyleObject => ({
      position: "fixed",
      left: 0,
      right: 0,
      top: 0,
      "z-index": 999,
    }),

    // 滚动容器
    scrollContainer: (height?: string | number): StyleObject => ({
      height: height || "100%",
      overflowY: "auto",
      "-webkit-overflow-scrolling": "touch",
    }),
  };
}

// 快捷方式导出
export const styleUtils = {
  // 主题相关
  theme: StyleUtils.currentTheme,
  setTheme: StyleUtils.setTheme.bind(StyleUtils),
  getThemeVar: StyleUtils.getThemeVar.bind(StyleUtils),

  // 单位转换
  rpx: StyleUtils.rpx.bind(StyleUtils),
  px: StyleUtils.px.bind(StyleUtils),
  vw: StyleUtils.vw.bind(StyleUtils),
  vh: StyleUtils.vh.bind(StyleUtils),

  // 样式处理
  merge: StyleUtils.mergeStyles.bind(StyleUtils),
  classNames: StyleUtils.classNames.bind(StyleUtils),
  styleToString: StyleUtils.styleToString.bind(StyleUtils),

  // 颜色处理
  alpha: StyleUtils.alpha.bind(StyleUtils),
  lighten: StyleUtils.lighten.bind(StyleUtils),
  darken: StyleUtils.darken.bind(StyleUtils),
  gradient: StyleUtils.gradient.bind(StyleUtils),

  // 阴影
  shadow: StyleUtils.shadow.bind(StyleUtils),
  shadowLayers: StyleUtils.shadowLayers.bind(StyleUtils),

  // 布局
  flex: StyleUtils.flex.bind(StyleUtils),
  grid: StyleUtils.grid.bind(StyleUtils),

  // 工具类
  utility: StyleUtils.utility,

  // uniapp 专用
  uniapp: StyleUtils.uniapp,
};

export default StyleUtils;