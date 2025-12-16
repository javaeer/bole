import { TemplateComponentResults } from "@/types/template-component";

export interface TemplateQuery extends BodyParams {
  name?: string;
  code?: string;
}

export interface TemplateResult {
  id: number;

  name: string;
  code: string;
  description: string;
  previewImage: string;
  isActive: boolean;
  version: string;
  globalStyle: GlobalStyle;
  globalLayout: GlobalLayout;
  components: TemplateComponentResults;

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

export interface FontSizes {
  h1: string;
  body: string;
}

export interface Spacing {
  sectionMargin: string;
  padding: string;
  lineHeight: string;
}

export interface GlobalStyle {
  theme: string;
  fontSizes: FontSizes;
  fontFamily: string;
  headerColor: string;
  primaryColor: string;
  accentColor: string;
  secondaryColor: string;
  backgroundColor: string;
  spacing: Spacing;
}

export interface GlobalLayout {
  type: string;
  columns: number;
  components: Array<{
    name: string
  }>;
  orientation: "portrait" | "landscape";
  componentOrder: Array<{
    name: string
  }>;
}

export interface ConfigTab {
  id: string;
  label: string;
  icon?: string;
}
