/**
 * 组合式函数 - 在 Vue3 Composition API 中使用
 */
import StyleUtils, { StyleObject, ThemeConfig } from "@/utils/style";

export function useStyle() {


  /**
   * 响应式样式计算
   */
  const computedStyle = (computeFn: () => StyleObject) => {
    return computed(computeFn);
  };

  /**
   * 动态类名
   */
  const dynamicClass = computed(() => {
    return (baseClass: string, conditionClasses: Record<string, boolean>) => {
      const classes = [baseClass];
      Object.entries(conditionClasses).forEach(([className, condition]) => {
        if (condition) {
          classes.push(className);
        }
      });
      return classes.join(" ");
    };
  });

  /**
   * 主题 Hook
   */
  const useTheme = () => {
    const theme = ref(StyleUtils.currentTheme);

    const setTheme = (config: Partial<ThemeConfig>) => {
      StyleUtils.setTheme(config);
      theme.value = StyleUtils.currentTheme;
    };

    const getColor = (type: keyof ThemeConfig, alpha?: number) => {
      const color = theme.value[type] as string;
      if (alpha !== undefined) {
        return StyleUtils.alpha(color, alpha);
      }
      return color;
    };

    return {
      theme,
      setTheme,
      getColor,
      cssVars: StyleUtils.getThemeCssVars(),
    };
  };

  /**
   * 响应式 Hook
   */
  const useResponsive = () => {
    const isMobile = ref(true);
    const isTablet = ref(false);
    const isDesktop = ref(false);

    // 在客户端更新
    if (typeof window !== "undefined") {
      const update = () => {
        const width = window.innerWidth;
        isMobile.value = width < 768;
        isTablet.value = width >= 768 && width < 992;
        isDesktop.value = width >= 992;
      };

      update();
      window.addEventListener("resize", update);

      onUnmounted(() => {
        window.removeEventListener("resize", update);
      });
    }

    return {
      isMobile,
      isTablet,
      isDesktop,
      currentBreakpoint: computed(() => {
        if (isDesktop.value) return "desktop";
        if (isTablet.value) return "tablet";
        return "mobile";
      }),
    };
  };

  return {
    StyleUtils,
    computedStyle,
    dynamicClass,
    useTheme,
    useResponsive,
  };
}
