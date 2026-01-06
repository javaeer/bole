//查询参数定义
import { TemplateComponentForm, TemplateComponentResult } from "@/types/template-component";

export interface TemplateQuery extends BodyQuery {
  name?: string;
  code?: string;
  collected?: boolean,
}

//响应类型定义
export interface TemplateResult {
  id: number;
  name: string;
  code: string;
  description?: string;
  previewImage?: string;
  isActive: boolean;
  version?: string;
  globalStyle?: TemplateGlobalStyle;
  globalLayout?: TemplateGlobalLayout;
  components?: TemplateComponentResult[];

  //通用字段
  createdAt?: string;
  updatedAt?: string;

  // 扩展前端需要的其他字段
  price: number;
  users: number;
  tags?: string[];
  category?: string;
  rating?: number;
}

// 模板字体大小配置
export interface TemplateFontSizes {
  h1: string;
  body: string;
}

//模板间距配置
export interface TemplateSpacing {
  sectionMargin: string;
  padding: string;
  lineHeight: string;
}

// 模板全局样式配置
export interface TemplateGlobalStyle {
  theme?: string;
  fontSizes?: TemplateFontSizes;
  fontFamily?: string;
  headerColor?: string;
  primaryColor?: string;
  accentColor?: string;
  secondaryColor?: string;
  backgroundColor?: string;
  spacing?: TemplateSpacing;
}

// 布局列配置
export interface TemplateLayoutColumns {
  left?: number;
  right?: number;
}

// 模板全局布局配置
export interface TemplateGlobalLayout {
  type?: string;
  columns?: TemplateLayoutColumns;
  orientation?: string;
  componentOrder?: string[];
}

//模板提交类型
export interface TemplateForm {
  id?: number;
  name: string;
  code: string;
  description?: string;
  previewImage?: string;
  isActive?: boolean;
  version?: string;
  globalStyle?: TemplateGlobalStyle;
  globalLayout?: TemplateGlobalLayout;
  components?: TemplateComponentForm[];
}
