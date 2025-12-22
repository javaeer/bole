//模板组件响应类型
export interface TemplateComponentItem {
  id: number;//非 组件ID
  deleted: number;
  name: string;
  key: string;
  defaultConfig?: string;
  templateId?: number;
  componentId: number;
  props: Record<string, any>;
  styles: Record<string, any>;
  createdAt?: string;
  updatedAt?: string;
}

//模板组件 提交 类型
export interface TemplateComponentForm {
  templateId?: number;
  componentId: number;
  props: Record<string, any>;
  styles: Record<string, any>;
}